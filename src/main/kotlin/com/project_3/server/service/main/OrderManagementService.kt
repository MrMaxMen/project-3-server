package com.project_3.server.service.main

import com.project_3.server.BuyerNotFoundException
import com.project_3.server.ItemNotFoundException
import com.project_3.server.PickupPointNotFoundException
import com.project_3.server.dto.OrderDTO
import com.project_3.server.models.Order
import com.project_3.server.models.OrderItem
import com.project_3.server.repos.BuyerRepository
import com.project_3.server.repos.CategoryRepository
import com.project_3.server.repos.ItemRepository
import com.project_3.server.repos.OrderRepository
import com.project_3.server.repos.PickupPointRepository
import com.project_3.server.repos.ProductRepository
import com.project_3.server.repos.SellerRepository
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
    private val pickupPointRepository: PickupPointRepository
) {

    @Transactional
    fun createOrder(buyerId : Long,newOrderDTO : OrderDTO) {

        val buyer = buyerRepository.findByIdOrNull(buyerId) ?: throw BuyerNotFoundException(buyerId)

        val pickupPoint = pickupPointRepository.findByIdOrNull(newOrderDTO.pickupPointId) ?: throw PickupPointNotFoundException(newOrderDTO.pickupPointId)



        val newOrder = Order(
            orderDateTime = LocalDateTime.now(),
            buyer = buyer,
            pickupPoint = pickupPoint,
        )

        val orderItemsList = mutableListOf<OrderItem>()



        for(orderItem in newOrderDTO.orderItems){
            val item = itemRepository.findByIdOrNull(orderItem.itemId) ?: throw ItemNotFoundException(orderItem.itemId)

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

        orderRepository.save(newOrder)

    }


}