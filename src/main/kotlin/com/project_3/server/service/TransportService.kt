package com.project_3.server.service

import com.project_3.server.dto.SupplyCreationDTO
import com.project_3.server.exceptions.DeliveryNotFoundByIdException
import com.project_3.server.exceptions.SellerNotFoundByIdException
import com.project_3.server.exceptions.VehicleNotFoundByIdException
import com.project_3.server.models.enums.ProductInOrderStatus
import com.project_3.server.models.enums.TransportationStatus
import com.project_3.server.models.logistics.transportation.Delivery
import com.project_3.server.models.order.ProductInOrder
import com.project_3.server.repos.DeliveryRepository
import com.project_3.server.repos.ProductInOrderRepository
import com.project_3.server.repos.SellerRepository
import com.project_3.server.repos.VehicleRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class TransportService(
    private val productInOrderRepository: ProductInOrderRepository,
    private val vehicleRepository: VehicleRepository,
    private val deliveryRepository: DeliveryRepository,
    private val sellerRepository: SellerRepository,
) {

    @Transactional
    @Scheduled(fixedRate = 1200000) // every 20 minutes // в будущем надо добавить реактивность
    fun deliveryCreationMonitor() {

        val pendingProductInOrders = productInOrderRepository.findAllByStatusAndDeliveryIsNull(ProductInOrderStatus.PENDING)

        val productInOrderListsByStock: List<List<ProductInOrder>> = pendingProductInOrders.groupBy { it.stock }.values.toList()

        for (productInOrderList in productInOrderListsByStock) {

            if (productInOrderList.isEmpty()) continue

            val stock = productInOrderList[0].stock

            var delivery = deliveryRepository.findBySourceStockAndStatus(stock, TransportationStatus.WAITING).firstOrNull()

            if (delivery == null) {

                val freeVehicle = vehicleRepository.findAllByIsFreeTrue().firstOrNull() ?: continue

                delivery =
                        Delivery(
                                sourceStock = stock,
                                vehicle = freeVehicle,
                                status = TransportationStatus.WAITING,
                                waitingTimeMinutes = 0
                        )

                freeVehicle.isFree = false

                vehicleRepository.save(freeVehicle)

                delivery = deliveryRepository.save(delivery)
            }


            for (productInOrder in productInOrderList) {

                val projectedWeightKg = delivery.currentWeightKg + ((productInOrder.quantity * productInOrder.product.weightGrams) / 1000.0)

                val projectedVolumeM3 = delivery.currentVolumeM3 + (productInOrder.quantity *
                        (productInOrder.product.lengthMm.toLong() * productInOrder.product.widthMm * productInOrder.product.heightMm) / 1_000_000_000.0)

                if (projectedWeightKg > delivery.vehicle.capacityKg || projectedVolumeM3 > delivery.vehicle.volumeM3) continue // Не влезает

                delivery.currentVolumeM3 = projectedVolumeM3
                delivery.currentWeightKg = projectedWeightKg
                productInOrder.delivery = delivery
                delivery.productInOrderList.add(productInOrder)
            }

            productInOrderRepository.saveAll(productInOrderList)


            val isTimeUp = delivery.waitingTimeMinutes >= 60

            val isWeightFull = delivery.currentWeightKg >= (delivery.vehicle.capacityKg - delivery.vehicle.capacityKg* 0.05) // 95% от веса

            val isVolumeFull = delivery.currentVolumeM3 >= (delivery.vehicle.volumeM3 - delivery.vehicle.volumeM3* 0.05) // 95% от объема


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

        //отправить уведомление водителю
    }


    @Transactional
    fun completeDelivery(deliveryId : Long){

        val delivery = deliveryRepository.findByIdOrNull(deliveryId) ?: throw DeliveryNotFoundByIdException(deliveryId)

        val vehicle = vehicleRepository.findByIdOrNull(delivery.vehicle.id!!) ?: throw VehicleNotFoundByIdException(delivery.vehicle.id!!)

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
    fun createTransfer(){}

    @Transactional
    fun createSupply(sellerId : Long, supplyCreationDTO: SupplyCreationDTO){



    }


    @Transactional
    @Scheduled(fixedRate = 1200000) // every 20 minutes
    fun supplyCreationMonitor(){



    }


    @Transactional
    fun completeTransportation(){}



}
