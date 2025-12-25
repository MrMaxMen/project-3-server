package com.project_3.server.service

import com.project_3.server.dto.OrderDTO
import com.project_3.server.exceptions.BuyerNotFoundByIdException
import com.project_3.server.exceptions.ItemNotFoundByIdException
import com.project_3.server.exceptions.PickupPointNotFoundByIdException
import com.project_3.server.models.Order
import com.project_3.server.models.OrderItem
import com.project_3.server.repos.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class OrderManagementService(
    private val productRepository: ProductRepository,
    private val itemRepository: ItemRepository,
    private val categoryRepository: CategoryRepository,
    private val sellerRepository: SellerRepository,
    private val orderRepository: OrderRepository,
    private val buyerRepository: BuyerRepository,
    private val pickupPointRepository: PickupPointRepository,
    private val stockRepository: StockRepository
) {

    @Transactional
    fun createOrder(buyerId : Long,newOrderDTO : OrderDTO) {

        val buyer = buyerRepository.findByIdOrNull(buyerId) ?: throw BuyerNotFoundByIdException(buyerId)

        val pickupPoint = pickupPointRepository.findByIdOrNull(newOrderDTO.pickupPointId) ?: throw PickupPointNotFoundByIdException(newOrderDTO.pickupPointId)



        val newOrder = Order(
            orderDateTime = LocalDateTime.now(),
            buyer = buyer,
            pickupPoint = pickupPoint,
        )

        val orderItemsList = mutableListOf<OrderItem>()



        for(orderItem in newOrderDTO.orderItems){
            val item = itemRepository.findByIdOrNull(orderItem.itemId) ?: throw ItemNotFoundByIdException(orderItem.itemId)

            val orderItem = OrderItem(
                order = newOrder, // Will be set when the Order is created
                item = item,
                quantity = orderItem.quantity,
                priceAtPurchase = orderItem.priceAtPurchase
            )

            orderItemsList.add(orderItem)
        }

        newOrder.orderItems = orderItemsList

        //нужно добавить списание айтемов со склада при создании заказа
        //нужно вычеслить ближайший склад к пункту выдачи и списать оттуда

        orderRepository.save(newOrder)

    }


}