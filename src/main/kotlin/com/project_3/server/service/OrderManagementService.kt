package com.project_3.server.service

import com.project_3.server.dto.OrderDTO
import com.project_3.server.dto.OrderItemDTO
import com.project_3.server.exceptions.BuyerNotFoundByIdException
import com.project_3.server.exceptions.ItemNotFoundByIdException
import com.project_3.server.exceptions.PickupPointNotFoundByIdException
import com.project_3.server.models.Order
import com.project_3.server.models.OrderItem
import com.project_3.server.models.Orthodrome
import com.project_3.server.models.Stock
import com.project_3.server.models.StockItem
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
    private val stockRepository: StockRepository,
    private val stockItemRepository: StockItemRepository,
    private val orthodromeRepository: OrthodromeRepository
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



        newOrderDTO.orderItems.forEach { orderItem ->

            val item = itemRepository.findByIdOrNull(orderItem.itemId) ?: throw ItemNotFoundByIdException(orderItem.itemId)

            val stockItems : List<StockItem> = stockItemRepository.findByItem(item)

            val stocks : MutableList<Stock> = mutableListOf()

            stockItems.forEach {
                stocks.add(it.stock)
            }

            val orthodromes : MutableList<Orthodrome> = mutableListOf()

            stocks.forEach {
                val orthodrome = orthodromeRepository.findByStockIdAndPickupPointId(it.id!!,pickupPoint.id!!)
                orthodromes.add(orthodrome!!)

            }

            val closestOrthodrome = orthodromes.minByOrNull { it.distanceKm }!!

            //shipment creation call

            val orderItem = OrderItem(
                order = newOrder, // Will be set when the Order is created
                item = item,
                quantity = orderItem.quantity,
                priceAtPurchase = orderItem.priceAtPurchase,
                stock = closestOrthodrome.stock,
            )

            val stockItem = stockItemRepository.findByItemAndStock(item,closestOrthodrome.stock)
            stockItem.availableQuantity -= orderItem.quantity

            orderItemsList.add(orderItem)
        }

        newOrder.orderItems = orderItemsList



        orderRepository.save(newOrder)

    }




}