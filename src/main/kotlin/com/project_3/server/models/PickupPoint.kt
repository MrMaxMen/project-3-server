package com.project_3.server.models

import jakarta.persistence.*

@Entity
class PickupPoint(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var name: String,

    var address: String,
    var latitude: Double,
    var longitude: Double,

    var workingHours: String? = null,

    @OneToMany(mappedBy = "pickupPoint", cascade = [CascadeType.ALL], orphanRemoval = true)
    val orders: MutableList<Order> = mutableListOf()
)
