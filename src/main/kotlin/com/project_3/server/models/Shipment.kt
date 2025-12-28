package com.project_3.server.models

import jakarta.persistence.*


@Entity
class Shipment (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    var orderItems: MutableList<OrderItem> = mutableListOf(),

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    var stock: Stock,


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    var pickupPoints: MutableList<PickupPoint> = mutableListOf(),


    @Enumerated(EnumType.STRING)
    var status: ShipmentStatus = ShipmentStatus.IN_TRANSIT


)

enum class ShipmentStatus {
    IN_TRANSIT,
    DELIVERED,
    CANCELLED
}