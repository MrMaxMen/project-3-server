package com.project_3.server.models

import jakarta.persistence.*

@Entity
class OrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    var order: Order,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    var item: Item,

    var quantity: Int ,

    var priceAtPurchase: Double


)