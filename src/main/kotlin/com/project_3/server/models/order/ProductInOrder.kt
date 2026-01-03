package com.project_3.server.models.order

import com.project_3.server.models.Product
import com.project_3.server.models.enums.ProductInOrderStatus
import com.project_3.server.models.logistics.transportation.Delivery
import com.project_3.server.models.stock.Stock
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class ProductInOrder(

        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(nullable = false) var order: Order,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(nullable = false)
        var product: Product,

        var quantity: Int,

        var priceAtPurchase: Double,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(nullable = false) var stock: Stock,

        @Enumerated(EnumType.STRING)
        var status: ProductInOrderStatus = ProductInOrderStatus.PENDING,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "delivery_id")
        var delivery: Delivery? = null
)
