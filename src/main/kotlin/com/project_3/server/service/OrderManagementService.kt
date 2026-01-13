package com.project_3.server.service

import com.project_3.server.dto.OrderDTO
import com.project_3.server.exceptions.BuyerNotFoundByIdException
import com.project_3.server.exceptions.DeliveryImpossibleException
import com.project_3.server.exceptions.PickupPointNotFoundByIdException
import com.project_3.server.exceptions.ProductNotFoundByIdException
import com.project_3.server.models.order.Order
import com.project_3.server.models.order.ProductInOrder
import com.project_3.server.models.stock.Stock
import com.project_3.server.repos.*
import java.time.LocalDateTime
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderManagementService(
        private val productRepository: ProductRepository,
        private val orderRepository: OrderRepository,
        private val buyerRepository: BuyerRepository,
        private val pickupPointRepository: PickupPointRepository,
        private val productOnStockRepository: ProductOnStockRepository,
        private val orthodromeRepository: OrthodromeRepository
) {

    @Transactional
    fun createOrder(buyerId: Long, newOrderDTO: OrderDTO) {

        val buyer =
                buyerRepository.findByIdOrNull(buyerId) ?: throw BuyerNotFoundByIdException(buyerId)

        val pickupPoint =
                pickupPointRepository.findByIdOrNull(newOrderDTO.pickupPointId)
                        ?: throw PickupPointNotFoundByIdException(newOrderDTO.pickupPointId)

        val newOrder =
                Order(
                        orderDateTime = LocalDateTime.now(),
                        buyer = buyer,
                        pickupPoint = pickupPoint,
                )

        val productInOrderList = mutableListOf<ProductInOrder>()

        newOrderDTO.orderProducts.forEach { productInOrderDTO ->

            val product = productRepository.findByIdOrNull(productInOrderDTO.productId) ?: throw ProductNotFoundByIdException(productInOrderDTO.productId)

            val productOnStockList = productOnStockRepository.findByProduct(product)

            val productsOnStockWithValidQuantity =
                    productOnStockList.filter { it.availableQuantity >= productInOrderDTO.quantity }

            val stocks = mutableListOf<Stock>()

            productsOnStockWithValidQuantity.forEach { stocks.add(it.stock) }

            val orthodromes = orthodromeRepository.findAllByPickupPointIdAndStockIdIn(pickupPoint.id!!,stocks.map { it.id!! })

            val closestOrthodrome = orthodromes.minByOrNull { it.distanceKm }
                ?: throw DeliveryImpossibleException("Невозможно доставить товар ${product.name} в выбранный ПВЗ: нет логистического маршрута")



            val productInOrder =
                    ProductInOrder(
                            order = newOrder,
                            product = product,
                            quantity = productInOrderDTO.quantity,
                            priceAtPurchase = productInOrderDTO.priceAtPurchase,
                            stock = closestOrthodrome.stock,
                    )

            val productOnStock = productOnStockRepository.findByProductAndStock(product, closestOrthodrome.stock)
                    ?: throw DeliveryImpossibleException("Товар ${product.name} отсутствует на складе ${closestOrthodrome.stock.name}")

            if (productOnStockRepository.reduceStock(id = productOnStock.id!!, quantity = productInOrder.quantity) == 0) { // решение проблемы взаимного исключения (можно лучше)
                throw DeliveryImpossibleException("Недостаточно товара ${product.name} на складе ${closestOrthodrome.stock.name}")
            }



            productInOrderList.add(productInOrder)

        }

        newOrder.productInOrders = productInOrderList

        orderRepository.save(newOrder)
    }

    @Transactional
    fun completeOrder(){}
}
