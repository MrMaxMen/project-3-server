package com.project_3.server.service

import com.project_3.server.dto.SupplyCreationDTO
import com.project_3.server.exceptions.DeliveryNotFoundByIdException
import com.project_3.server.exceptions.ProductNotFoundByIdException
import com.project_3.server.exceptions.SupplyBatchVolumeExceededException
import com.project_3.server.exceptions.SupplyBatchWeightExceededException
import com.project_3.server.exceptions.VehicleNotFoundByIdException
import com.project_3.server.models.ProductBatch
import com.project_3.server.models.enums.ProductInOrderStatus
import com.project_3.server.models.enums.TransferRequestStatus
import com.project_3.server.models.enums.TransportationStatus
import com.project_3.server.models.logistics.transportation.Delivery
import com.project_3.server.models.logistics.transportation.Supply
import com.project_3.server.models.logistics.transportation.Transfer
import com.project_3.server.models.order.ProductInOrder
import com.project_3.server.repos.DeliveryRepository
import com.project_3.server.repos.ProductInOrderRepository
import com.project_3.server.repos.ProductRepository
import com.project_3.server.repos.SellerRepository
import com.project_3.server.repos.StockRepository
import com.project_3.server.repos.SupplyRepository
import com.project_3.server.repos.TransferRepository
import com.project_3.server.repos.TransferRequestRepository
import com.project_3.server.repos.VehicleRepository
import java.time.LocalDateTime
import kotlin.math.min
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TransportService(
        private val productInOrderRepository: ProductInOrderRepository,
        private val vehicleRepository: VehicleRepository,
        private val deliveryRepository: DeliveryRepository,
        private val sellerRepository: SellerRepository,
        private val orthodromeService: OrthodromeService,
        private val supplyRepository: SupplyRepository,
        private val productRepository: ProductRepository,
        private val stockRepository: StockRepository,
        private val transferRepository: TransferRepository,
        private val transferRequestRepository: TransferRequestRepository,
) {

        companion object {
                const val MIN_WEIGHT_KG =
                        500.0 // Минимальный вес для формирования рейса (вместимость самого
                // маленького
                // грузовика)
                const val MIN_VOLUME_M3 = 3.0 // Минимальный объём для формирования рейса
                const val MAX_WAIT_MINUTES =
                        60 // Максимальное время ожидания перед принудительной отправкой
                const val FILL_THRESHOLD = 0.95 // Порог заполнения (95%) для отправки
                const val MONITOR_INTERVAL_MS = 1200000L // 20 минут
        }

        /**
         * Монитор 1: Формирование рейсов
         *
         * Достаёт ProductInOrder со статусом PENDING без привязки к delivery, группирует по складу
         * и формирует рейсы (Delivery) со статусом WAITING_FOR_VEHICLE. Рейсы заполняются до
         * MIN_WEIGHT_KG / MIN_VOLUME_M3 — это гарантирует, что рейс поместится в любой грузовик.
         */
        @Transactional
        @Scheduled(fixedRate = 1200000)
        fun deliveryFormationMonitor() {

                val pendingProductInOrders =
                        productInOrderRepository.findAllByStatusAndDeliveryIsNull(
                                ProductInOrderStatus.PENDING
                        )

                val productInOrderListsByStock = pendingProductInOrders.groupBy { it.stock }.values

                for (productInOrderList in productInOrderListsByStock) {

                        if (productInOrderList.isEmpty()) continue

                        val stock = productInOrderList[0].stock
                        var isNewDelivery = false

                        // Ищем существующий рейс WAITING_FOR_VEHICLE для этого склада
                        var delivery =
                                deliveryRepository
                                        .findAllBySourceStockAndStatus(
                                                stock,
                                                TransportationStatus.WAITING_FOR_VEHICLE
                                        )
                                        .firstOrNull()

                        // Если нет — создаём новый (без машины)
                        if (delivery == null) {
                                delivery =
                                        Delivery(
                                                sourceStock = stock,
                                                status = TransportationStatus.WAITING_FOR_VEHICLE,
                                                waitingTimeMinutes = 0
                                        )
                                delivery = deliveryRepository.save(delivery)
                                isNewDelivery = true
                        }

                        val addedProducts = mutableListOf<ProductInOrder>()

                        // Добавляем продукты пока не достигнем MIN capacity
                        for (productInOrder in productInOrderList) {

                                val projectedWeightKg =
                                        delivery.currentWeightKg +
                                                ((productInOrder.quantity *
                                                        productInOrder.product.weightGrams) /
                                                        1000.0)

                                val projectedVolumeM3 =
                                        delivery.currentVolumeM3 +
                                                (productInOrder.quantity *
                                                        (productInOrder.product.lengthMm.toLong() *
                                                                productInOrder.product.widthMm *
                                                                productInOrder.product.heightMm) /
                                                        1_000_000_000.0)

                                // Не превышаем минимальные лимиты (гарантия влезания в любой
                                // грузовик)
                                if (projectedWeightKg > MIN_WEIGHT_KG ||
                                                projectedVolumeM3 > MIN_VOLUME_M3
                                )
                                        continue

                                delivery.currentVolumeM3 = projectedVolumeM3
                                delivery.currentWeightKg = projectedWeightKg
                                productInOrder.delivery = delivery
                                delivery.productInOrderList.add(productInOrder)
                                addedProducts.add(productInOrder)
                        }

                        if (addedProducts.isNotEmpty()) {
                                productInOrderRepository.saveAll(addedProducts)
                        }

                        // Увеличиваем время ожидания только если рейс уже существовал (ждал)
                        if (!isNewDelivery) {
                                delivery.waitingTimeMinutes += 20
                        }
                        deliveryRepository.save(delivery)
                }
        }

        /**
         * Монитор 2: Назначение машин и отправка
         *
         * Достаёт рейсы со статусом WAITING_FOR_VEHICLE, назначает им свободные машины, дозаполняет
         * до вместимости конкретной машины, и отправляет когда:
         * - время ожидания >= MAX_WAIT_MINUTES, или
         * - заполнено >= 95% по весу или объёму
         */
        @Transactional
        @Scheduled(initialDelay = 60000, fixedRate = 1200000)
        fun deliveryDispatchMonitor() {

                val waitingForVehicleDeliveries =
                        deliveryRepository.findAllByStatus(TransportationStatus.WAITING_FOR_VEHICLE)
                                .sortedByDescending { it.waitingTimeMinutes }

                val freeVehicles = vehicleRepository.findAllByIsFreeTrue().toMutableList()

                for (delivery in waitingForVehicleDeliveries) {
                        if (freeVehicles.isEmpty()) break

                        val vehicle = freeVehicles.removeFirst()
                        delivery.vehicle = vehicle
                        delivery.status = TransportationStatus.WAITING
                        vehicle.isFree = false

                        vehicleRepository.save(vehicle)
                        deliveryRepository.save(delivery)
                }

                val allWaitingDeliveries =
                        deliveryRepository.findAllByStatus(TransportationStatus.WAITING)

                for (delivery in allWaitingDeliveries) {

                        val vehicle = delivery.vehicle ?: continue

                        val pendingProductsForStock =
                                productInOrderRepository.findAllByStatusAndDeliveryIsNullAndStock(
                                        status = ProductInOrderStatus.PENDING,
                                        stock = delivery.sourceStock
                                )

                        for (productInOrder in pendingProductsForStock) {

                                val projectedWeightKg =
                                        delivery.currentWeightKg +
                                                ((productInOrder.quantity *
                                                        productInOrder.product.weightGrams) /
                                                        1000.0)

                                val projectedVolumeM3 =
                                        delivery.currentVolumeM3 +
                                                (productInOrder.quantity *
                                                        (productInOrder.product.lengthMm.toLong() *
                                                                productInOrder.product.widthMm *
                                                                productInOrder.product.heightMm) /
                                                        1_000_000_000.0)

                                // Проверяем вместимость конкретной машины
                                if (projectedWeightKg > vehicle.capacityKg ||
                                                projectedVolumeM3 > vehicle.volumeM3
                                )
                                        continue

                                delivery.currentVolumeM3 = projectedVolumeM3
                                delivery.currentWeightKg = projectedWeightKg
                                productInOrder.delivery = delivery
                                delivery.productInOrderList.add(productInOrder)

                                productInOrderRepository.save(productInOrder)
                        }

                        val isTimeUp = delivery.waitingTimeMinutes >= MAX_WAIT_MINUTES
                        val isWeightFull =
                                delivery.currentWeightKg >= (vehicle.capacityKg * FILL_THRESHOLD)
                        val isVolumeFull =
                                delivery.currentVolumeM3 >= (vehicle.volumeM3 * FILL_THRESHOLD)

                        if (isTimeUp || isWeightFull || isVolumeFull) {
                                dispatchDelivery(delivery)
                        } else {
                                delivery.waitingTimeMinutes += 20
                                deliveryRepository.save(delivery)
                        }
                }
        }

        @Transactional
        private fun dispatchDelivery(delivery: Delivery) {

                delivery.status = TransportationStatus.IN_TRANSIT
                delivery.departureTime = LocalDateTime.now() // под вопросом
                delivery.productInOrderList.forEach { product ->
                        product.status = ProductInOrderStatus.IN_TRANSIT
                }

                productInOrderRepository.saveAll(delivery.productInOrderList)
                deliveryRepository.save(delivery)
                // отправить уведомление водителю
        }

        @Transactional
        fun completeDelivery(deliveryId: Long) {

                val delivery =
                        deliveryRepository.findByIdOrNull(deliveryId)
                                ?: throw DeliveryNotFoundByIdException(deliveryId)

                val vehicle =
                        vehicleRepository.findByIdOrNull(delivery.vehicle!!.id!!)
                                ?: throw VehicleNotFoundByIdException(delivery.vehicle!!.id!!)

                delivery.status = TransportationStatus.DELIVERED

                delivery.productInOrderList.forEach { product ->
                        product.status = ProductInOrderStatus.DELIVERED
                }

                vehicle.isFree = true

                productInOrderRepository.saveAll(delivery.productInOrderList)
                vehicleRepository.save(vehicle)
                deliveryRepository.save(delivery)
        }

        /**
         * Создание поставки от продавца на склад.
         *
         * Находит ближайший склад, создаёт Supply со статусом WAITING_FOR_VEHICLE. Назначение
         * машины происходит в supplyDispatchMonitor().
         */
        @Transactional
        fun createSupply(
                supplyCreationDTO: SupplyCreationDTO
        ) { // ограничение по весу/объёму на клиенте

                val closestStock =
                        orthodromeService.closestStockForAddress(
                                supplyCreationDTO.sourceLatitude,
                                supplyCreationDTO.sourceLongitude
                        )

                val supply =
                        Supply(
                                sourceAddress = supplyCreationDTO.sourceAddress,
                                sourceLatitude = supplyCreationDTO.sourceLatitude,
                                sourceLongitude = supplyCreationDTO.sourceLongitude,
                                status = TransportationStatus.WAITING_FOR_VEHICLE,
                                destinationStock = closestStock
                        )

                val products = mutableListOf<ProductBatch>()

                supplyCreationDTO.productBatchDTOList.forEach {
                        val product =
                                productRepository.findByIdOrNull(it.productId)
                                        ?: throw ProductNotFoundByIdException(it.productId)
                        val productBatch =
                                ProductBatch(
                                        product = product,
                                        quantity = it.quantity,
                                        supply = supply,
                                )
                        products.add(productBatch)
                }

                val totalWeightKg =
                        products.sumOf { (it.product.weightGrams * it.quantity) / 1000.0 }

                val totalVolumeM3 =
                        products.sumOf {
                                (it.product.lengthMm.toLong() *
                                        it.product.widthMm *
                                        it.product.heightMm *
                                        it.quantity) / 1_000_000_000.0
                        }

                if (totalWeightKg > MIN_WEIGHT_KG) {
                        throw SupplyBatchWeightExceededException()
                }

                if (totalVolumeM3 > MIN_VOLUME_M3) {
                        throw SupplyBatchVolumeExceededException()
                }

                supply.productBatchList = products
                supplyRepository.save(supply)
        }

        /**
         * Монитор назначения машин для поставок.
         *
         * Достаёт Supply со статусом WAITING_FOR_VEHICLE, назначает свободные машины и отправляет в
         * IN_TRANSIT.
         */
        @Transactional
        @Scheduled(fixedRate = 1200000)
        fun supplyDispatchMonitor() {

                val supplies =
                        supplyRepository.findALLByStatus(TransportationStatus.WAITING_FOR_VEHICLE)
                                // Сортировка по ID как прокси времени создания (FIFO) для честности
                                .sortedBy { it.id }

                val freeVehicles = vehicleRepository.findAllByIsFreeTrue().toMutableList()

                val bound = min(freeVehicles.size, supplies.size)

                for (i in 0 until bound) {
                        val supply = supplies[i]
                        val vehicle = freeVehicles[i]

                        supply.vehicle = vehicle
                        supply.status = TransportationStatus.IN_TRANSIT
                        supply.departureTime =
                                LocalDateTime.now() // Устанавливаем время отправления
                        vehicle.isFree = false

                        supplyRepository.save(supply)
                        vehicleRepository.save(vehicle)
                }
        }

        /**
         * Монитор 1: Формирование рейсов Transfer
         *
         * Достаёт TransferRequest со статусом PENDING, группирует по маршруту (sourceStock →
         * destinationStock), и формирует рейсы Transfer со статусом WAITING_FOR_VEHICLE. Рейсы
         * заполняются до MIN_WEIGHT_KG / MIN_VOLUME_M3.
         */
        @Transactional
        @Scheduled(fixedRate = 1200000)
        fun transferFormationMonitor() {

                val pendingRequests =
                        transferRequestRepository.findAllByStatusAndTransferIsNull(
                                TransferRequestStatus.PENDING
                        )

                // Группируем по маршруту (sourceStock → destinationStock)
                val requestsByRoute =
                        pendingRequests.groupBy { Pair(it.sourceStock.id, it.destinationStock.id) }

                for ((_, requests) in requestsByRoute) {

                        if (requests.isEmpty()) continue

                        val sourceStock = requests[0].sourceStock
                        val destinationStock = requests[0].destinationStock
                        var isNewTransfer = false

                        // Ищем существующий рейс WAITING_FOR_VEHICLE для этого маршрута
                        var transfer =
                                transferRepository
                                        .findAllBySourceStockAndDestinationStockAndStatus(
                                                sourceStock,
                                                destinationStock,
                                                TransportationStatus.WAITING_FOR_VEHICLE
                                        )
                                        .firstOrNull()

                        // Если нет — создаём новый (без машины)
                        // Если нет — создаём новый (без машины)
                        if (transfer == null) {
                                transfer =
                                        Transfer(
                                                sourceStock = sourceStock,
                                                destinationStock = destinationStock,
                                                status = TransportationStatus.WAITING_FOR_VEHICLE,
                                                waitingTimeMinutes = 0
                                        )
                                transfer = transferRepository.save(transfer)
                                isNewTransfer = true
                        }

                        // Добавляем партии пока не достигнем MIN capacity
                        for (request in requests) {

                                val batch = request.productBatch
                                val batchWeightKg =
                                        (batch.product.weightGrams * batch.quantity) / 1000.0
                                val batchVolumeM3 =
                                        (batch.product.lengthMm.toLong() *
                                                batch.product.widthMm *
                                                batch.product.heightMm *
                                                batch.quantity) / 1_000_000_000.0

                                val projectedWeightKg = transfer.currentWeightKg + batchWeightKg
                                val projectedVolumeM3 = transfer.currentVolumeM3 + batchVolumeM3

                                // Не превышаем минимальные лимиты
                                if (projectedWeightKg > MIN_WEIGHT_KG ||
                                                projectedVolumeM3 > MIN_VOLUME_M3
                                )
                                        continue

                                transfer.currentWeightKg = projectedWeightKg
                                transfer.currentVolumeM3 = projectedVolumeM3

                                batch.transfer = transfer // Cascade сохранит batch
                                transfer.productBatchList.add(batch)

                                request.transfer = transfer
                                request.status = TransferRequestStatus.ASSIGNED
                                transfer.transferRequests.add(request)

                                transferRequestRepository.save(request)
                        }

                        if (!isNewTransfer) {
                                transfer.waitingTimeMinutes += 20
                        }
                        transferRepository.save(transfer)
                }
        }

        /**
         * Монитор 2: Назначение машин и отправка Transfer
         *
         * Достаёт рейсы со статусом WAITING_FOR_VEHICLE, назначает им свободные машины, дозаполняет
         * целыми партиями до вместимости конкретной машины, и отправляет когда таймаут или
         * заполнено на 95%.
         */
        @Transactional
        @Scheduled(initialDelay = 60000, fixedRate = 1200000)
        fun transferDispatchMonitor() {

                // Достаём рейсы ожидающие машину (приоритет — дольше ждущие)
                val waitingForVehicleTransfers =
                        transferRepository.findAllByStatus(TransportationStatus.WAITING_FOR_VEHICLE)
                                .sortedByDescending { it.waitingTimeMinutes }

                val freeVehicles = vehicleRepository.findAllByIsFreeTrue().toMutableList()

                // Шаг 1: Назначаем машины рейсам без машин
                for (transfer in waitingForVehicleTransfers) {
                        if (freeVehicles.isEmpty()) break

                        val vehicle = freeVehicles.removeFirst()
                        transfer.vehicle = vehicle
                        transfer.status = TransportationStatus.WAITING
                        vehicle.isFree = false

                        vehicleRepository.save(vehicle)
                        transferRepository.save(transfer)
                }

                // Шаг 2: Обрабатываем все рейсы с машинами
                val allWaitingTransfers =
                        transferRepository.findAllByStatus(TransportationStatus.WAITING)

                for (transfer in allWaitingTransfers) {

                        val vehicle = transfer.vehicle ?: continue

                        // Дозаполняем рейс целыми партиями до вместимости машины
                        val pendingRequestsForRoute =
                                transferRequestRepository
                                        .findAllByStatusAndSourceStockAndDestinationStock(
                                                status = TransferRequestStatus.PENDING,
                                                sourceStock = transfer.sourceStock,
                                                destinationStock = transfer.destinationStock
                                        )

                        for (request in pendingRequestsForRoute) {

                                val batch = request.productBatch
                                val batchWeightKg =
                                        (batch.product.weightGrams * batch.quantity) / 1000.0
                                val batchVolumeM3 =
                                        (batch.product.lengthMm.toLong() *
                                                batch.product.widthMm *
                                                batch.product.heightMm *
                                                batch.quantity) / 1_000_000_000.0

                                val projectedWeightKg = transfer.currentWeightKg + batchWeightKg
                                val projectedVolumeM3 = transfer.currentVolumeM3 + batchVolumeM3

                                // Проверяем вместимость конкретной машины (целая партия или ничего)
                                if (projectedWeightKg > vehicle.capacityKg ||
                                                projectedVolumeM3 > vehicle.volumeM3
                                )
                                        continue

                                transfer.currentWeightKg = projectedWeightKg
                                transfer.currentVolumeM3 = projectedVolumeM3

                                batch.transfer = transfer
                                transfer.productBatchList.add(batch)

                                request.transfer = transfer
                                request.status = TransferRequestStatus.ASSIGNED
                                transfer.transferRequests.add(request)

                                transferRequestRepository.save(request)
                        }

                        // Проверяем условия отправки
                        val isTimeUp = transfer.waitingTimeMinutes >= MAX_WAIT_MINUTES
                        val isWeightFull =
                                transfer.currentWeightKg >= (vehicle.capacityKg * FILL_THRESHOLD)
                        val isVolumeFull =
                                transfer.currentVolumeM3 >= (vehicle.volumeM3 * FILL_THRESHOLD)

                        if (isTimeUp || isWeightFull || isVolumeFull) {
                                dispatchTransfer(transfer)
                        } else {
                                transfer.waitingTimeMinutes += 20
                                transferRepository.save(transfer)
                        }
                }
        }

        @Transactional
        private fun dispatchTransfer(transfer: Transfer) {
                transfer.status = TransportationStatus.IN_TRANSIT
                transfer.departureTime = LocalDateTime.now()

                transfer.transferRequests.forEach { request ->
                        request.status = TransferRequestStatus.IN_TRANSIT
                }

                transferRequestRepository.saveAll(transfer.transferRequests)
                transferRepository.save(transfer)
        }

        @Transactional fun completeTransfer() {}

        @Transactional fun completeSupply() {}
}
