package com.project_3.server.models

import com.project_3.server.models.enums.OrderItemStatus
import com.project_3.server.models.stock.Stock
import jakarta.persistence.*

@Entity
class ProductInOrder(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    var order: Order,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    var product: Product,

    var quantity: Int,

    var priceAtPurchase: Double,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    var stock: Stock,


    @Enumerated(EnumType.STRING)
    var status: OrderItemStatus = OrderItemStatus.PENDING


)

