package com.project_3.server.models.delivery

import com.project_3.server.models.Order
import com.project_3.server.models.delivery.Orthodrome
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class PickupPoint(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var name: String,

    var address: String,

    var latitude: Double,
    var longitude: Double,

    @OneToMany(mappedBy = "pickupPoint", cascade = [CascadeType.ALL], orphanRemoval = true)
    val orthodromes: MutableList<Orthodrome> = mutableListOf(),

    @OneToMany(mappedBy = "pickupPoint", cascade = [CascadeType.ALL], orphanRemoval = true)
    val orders: MutableSet<Order> = mutableSetOf()
)