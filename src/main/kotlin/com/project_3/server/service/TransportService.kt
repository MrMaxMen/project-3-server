package com.project_3.server.service

import com.project_3.server.dto.SupplyCreationDTO
import com.project_3.server.exceptions.DeliveryNotFoundByIdException
import com.project_3.server.exceptions.ProductNotFoundByIdException
import com.project_3.server.exceptions.SupplyBatchVolumeExceededException
import com.project_3.server.exceptions.SupplyBatchWeightExceededException
import com.project_3.server.exceptions.SupplyNotFoundByIdException
import com.project_3.server.exceptions.TransferNotFoundByIdException
import com.project_3.server.exceptions.VehicleNotFoundByIdException
import com.project_3.server.models.ProductBatch
import com.project_3.server.models.enums.ProductInOrderStatus
import com.project_3.server.models.enums.TransferRequestStatus
import com.project_3.server.models.enums.TransportationStatus
import com.project_3.server.models.logistics.transportation.Delivery
import com.project_3.server.models.logistics.transportation.Supply
import com.project_3.server.models.logistics.transportation.Transfer
import com.project_3.server.models.stock.ProductOnStock
import com.project_3.server.repos.DeliveryRepository
import com.project_3.server.repos.ProductInOrderRepository
import com.project_3.server.repos.ProductOnStockRepository
import com.project_3.server.repos.ProductRepository
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
        private val orthodromeService: OrthodromeService,
        private val supplyRepository: SupplyRepository,
        private val productRepository: ProductRepository,
        private val transferRepository: TransferRepository,
        private val transferRequestRepository: TransferRequestRepository,
        private val productOnStockRepository: ProductOnStockRepository
) {

        companion object {
                const val MIN_WEIGHT_KG = 500.0
                const val MIN_VOLUME_M3 = 3.0
                const val MAX_WAIT_MINUTES = 60
                const val FILL_THRESHOLD = 0.95
                const val MONITOR_INTERVAL_MS = 1200000L
        }

        @Transactional
        @Scheduled(fixedRate = MONITOR_INTERVAL_MS)
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

                        var delivery =
                                deliveryRepository
                                        .findAllBySourceStockAndStatus(
                                                stock,
                                                TransportationStatus.WAITING_FOR_VEHICLE
                                        )
                                        .firstOrNull()

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

                                if (projectedWeightKg > MIN_WEIGHT_KG ||
                                                projectedVolumeM3 > MIN_VOLUME_M3
                                )
                                        continue

                                delivery.currentVolumeM3 = projectedVolumeM3
                                delivery.currentWeightKg = projectedWeightKg
                                productInOrder.delivery = delivery
                                delivery.productInOrderList.add(productInOrder)
                        }

                        if (!isNewDelivery) delivery.waitingTimeMinutes += 20

                        deliveryRepository.save(delivery)
                }
        }

        @Transactional
        @Scheduled(initialDelay = 60000, fixedRate = MONITOR_INTERVAL_MS)
        fun deliveryDispatchMonitor() {

                // закрепление машины
                val deliveriesWaitingForVehicle =
                        deliveryRepository.findAllByStatus(TransportationStatus.WAITING_FOR_VEHICLE)
                                .sortedByDescending { it.waitingTimeMinutes }

                val freeVehicles = vehicleRepository.findAllByIsFreeTrue().toMutableList()

                for (delivery in deliveriesWaitingForVehicle) {

                        if (freeVehicles.isEmpty()) break

                        val vehicle = freeVehicles.removeFirst()
                        delivery.vehicle = vehicle
                        delivery.status = TransportationStatus.WAITING
                        vehicle.isFree = false

                        vehicleRepository.save(vehicle)
                        deliveryRepository.save(delivery)
                }

                // дозагрузка
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

                                delivery.status = TransportationStatus.IN_TRANSIT
                                delivery.departureTime = LocalDateTime.now() // под вопросом
                                delivery.productInOrderList.forEach { product ->
                                        product.status = ProductInOrderStatus.IN_TRANSIT
                                }

                                productInOrderRepository.saveAll(delivery.productInOrderList)
                                deliveryRepository.save(delivery)
                                // отправить уведомление водителю

                        } else {
                                delivery.waitingTimeMinutes += 20
                                deliveryRepository.save(delivery)
                        }
                }
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

        @Transactional
        fun createSupply(supplyCreationDTO: SupplyCreationDTO){ // ограничение по весу/объёму на клиенте

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

                val productBatches = mutableListOf<ProductBatch>()

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

                        productBatches.add(productBatch)
                }

                val totalWeightKg =
                        productBatches.sumOf { (it.product.weightGrams * it.quantity) / 1000.0 }

                val totalVolumeM3 =
                        productBatches.sumOf {
                                (it.product.lengthMm.toLong() *
                                        it.product.widthMm *
                                        it.product.heightMm *
                                        it.quantity) / 1_000_000_000.0
                        }

                if (totalWeightKg > MIN_WEIGHT_KG) throw SupplyBatchWeightExceededException()

                if (totalVolumeM3 > MIN_VOLUME_M3) throw SupplyBatchVolumeExceededException()


                supply.productBatchList = productBatches
                supplyRepository.save(supply)
        }

        @Transactional
        @Scheduled(fixedRate = MONITOR_INTERVAL_MS)
        fun supplyDispatchMonitor() {

                val waitingSupplies =
                        supplyRepository.findALLByStatus(TransportationStatus.WAITING_FOR_VEHICLE)
                                .sortedBy { it.id }// Сортировка по ID как прокси времени создания (FIFO) для честности

                val freeVehicles = vehicleRepository.findAllByIsFreeTrue()

                val bound = min(freeVehicles.size, waitingSupplies.size)

                for (i in 0 until bound) {

                        val supply = waitingSupplies[i]
                        val vehicle = freeVehicles[i]

                        supply.vehicle = vehicle
                        supply.status = TransportationStatus.IN_TRANSIT
                        supply.departureTime = LocalDateTime.now() // под вопросом
                        vehicle.isFree = false

                        supplyRepository.save(supply)
                        vehicleRepository.save(vehicle)
                }
        }

        @Transactional
        @Scheduled(fixedRate = MONITOR_INTERVAL_MS)
        fun transferFormationMonitor() {

                val pendingRequests = transferRequestRepository.findAllByStatusAndTransferIsNull(TransferRequestStatus.PENDING)

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
                                                projectedVolumeM3 > MIN_VOLUME_M3) continue

                                transfer.currentWeightKg = projectedWeightKg
                                transfer.currentVolumeM3 = projectedVolumeM3

                                batch.transfer = transfer // Cascade сохранит batch
                                transfer.productBatchList.add(batch)

                                val productOnStockSource =
                                        productOnStockRepository.findByProductAndStock(batch.product, sourceStock)
                                                ?: throw ProductNotFoundByIdException(batch.product.id!!)

                                productOnStockSource.availableQuantity -= batch.quantity

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

        @Transactional
        @Scheduled(initialDelay = 60000, fixedRate = MONITOR_INTERVAL_MS)
        fun transferDispatchMonitor() {

                val waitingForVehicleTransfers =
                        transferRepository.findAllByStatus(TransportationStatus.WAITING_FOR_VEHICLE)
                                .sortedByDescending { it.waitingTimeMinutes }

                val freeVehicles = vehicleRepository.findAllByIsFreeTrue().toMutableList()

                //назначение машин на рейсы
                for (transfer in waitingForVehicleTransfers) {
                        if (freeVehicles.isEmpty()) break

                        val vehicle = freeVehicles.removeFirst()
                        transfer.vehicle = vehicle
                        transfer.status = TransportationStatus.WAITING
                        vehicle.isFree = false

                        vehicleRepository.save(vehicle)
                        transferRepository.save(transfer)
                }

                //дозагрузка рейсов
                val allWaitingTransfers =
                        transferRepository.findAllByStatus(TransportationStatus.WAITING)

                for (transfer in allWaitingTransfers) {

                        val vehicle = transfer.vehicle ?: continue

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
                                                projectedVolumeM3 > vehicle.volumeM3) continue

                                transfer.currentWeightKg = projectedWeightKg
                                transfer.currentVolumeM3 = projectedVolumeM3

                                batch.transfer = transfer
                                transfer.productBatchList.add(batch)

                                request.transfer = transfer
                                request.status = TransferRequestStatus.ASSIGNED
                                transfer.transferRequests.add(request)

                                transferRequestRepository.save(request)
                        }


                        val isTimeUp = transfer.waitingTimeMinutes >= MAX_WAIT_MINUTES

                        val isWeightFull = transfer.currentWeightKg >= (vehicle.capacityKg * FILL_THRESHOLD)

                        val isVolumeFull = transfer.currentVolumeM3 >= (vehicle.volumeM3 * FILL_THRESHOLD)

                        if (isTimeUp || isWeightFull || isVolumeFull) {

                                transfer.status = TransportationStatus.IN_TRANSIT
                                transfer.departureTime = LocalDateTime.now() // под вопросом

                                transfer.transferRequests.forEach { request ->
                                        request.status = TransferRequestStatus.IN_TRANSIT
                                }

                                transferRequestRepository.saveAll(transfer.transferRequests)
                                transferRepository.save(transfer)
                                // отправить уведомление водителю
                        } else {

                                transfer.waitingTimeMinutes += 20
                                transferRepository.save(transfer)
                        }
                }
        }

        @Transactional
        fun completeTransfer(transferId: Long) {

                val transfer = transferRepository.findByIdOrNull(transferId)
                                ?: throw TransferNotFoundByIdException(transferId)

                val vehicle = vehicleRepository.findByIdOrNull(transfer.vehicle!!.id!!)
                                ?: throw VehicleNotFoundByIdException(transfer.vehicle!!.id!!)

                transfer.status = TransportationStatus.DELIVERED

                transfer.transferRequests.forEach { request ->
                        request.status = TransferRequestStatus.COMPLETED
                }

                vehicle.isFree = true

                transfer.productBatchList.forEach { batch ->

                        val product = productRepository.findByIdOrNull(batch.product.id!!)
                                        ?: throw ProductNotFoundByIdException(batch.product.id!!)

                        val productOnStockSource =
                                productOnStockRepository.findByProductAndStock(product, transfer.sourceStock)
                                        ?: throw ProductNotFoundByIdException(product.id!!)


                        var productOnStockDestination =
                                productOnStockRepository.findByProductAndStock(product, transfer.destinationStock)

                        if (productOnStockDestination == null) {

                                productOnStockDestination =
                                        ProductOnStock(
                                                stock = transfer.destinationStock,
                                                product = product,
                                                realQuantity = 0,
                                                availableQuantity = 0
                                        )

                        }


                        productOnStockSource.realQuantity -= batch.quantity

                        productOnStockDestination.realQuantity += batch.quantity
                        productOnStockDestination.availableQuantity += batch.quantity

                        productOnStockRepository.save(productOnStockSource)
                        productOnStockRepository.save(productOnStockDestination)
                }

                transferRequestRepository.saveAll(transfer.transferRequests)
                vehicleRepository.save(vehicle)
                transferRepository.save(transfer)
        }

        @Transactional
        fun completeSupply(supplyId: Long) {

                val supply = supplyRepository.findByIdOrNull(supplyId)
                                ?: throw SupplyNotFoundByIdException(supplyId)

                val vehicle = vehicleRepository.findByIdOrNull(supply.vehicle!!.id!!)
                                ?: throw VehicleNotFoundByIdException(supply.vehicle!!.id!!)

                supply.status = TransportationStatus.DELIVERED

                vehicle.isFree = true

                supply.productBatchList.forEach { batch ->

                        val product = productRepository.findByIdOrNull(batch.product.id!!)
                                        ?: throw ProductNotFoundByIdException(batch.product.id!!)

                        var productOnStockDestination =
                                productOnStockRepository.findByProductAndStock(product, supply.destinationStock)

                        if (productOnStockDestination == null) {

                                productOnStockDestination =
                                        ProductOnStock(
                                                stock = supply.destinationStock,
                                                product = product,
                                                realQuantity = 0,
                                                availableQuantity = 0
                                        )

                        }

                        productOnStockDestination.realQuantity += batch.quantity
                        productOnStockDestination.availableQuantity += batch.quantity

                        productOnStockRepository.save(productOnStockDestination)
                }

                vehicleRepository.save(vehicle)
                supplyRepository.save(supply)
        }
}
