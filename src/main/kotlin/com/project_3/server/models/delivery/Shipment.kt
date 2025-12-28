package com.project_3.server.models.delivery

import com.project_3.server.models.ProductInOrder
import com.project_3.server.models.enums.ShipmentStatus
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
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne

@Entity
class Shipment(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @OneToMany(fetch = FetchType.LAZY)
        @JoinColumn(nullable = false)
        var productInOrders: MutableList<ProductInOrder> = mutableListOf(),

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(nullable = false)
        var stock: Stock,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(nullable = false)
        var pickupPoint: PickupPoint,

        @Enumerated(EnumType.STRING)
        var status: ShipmentStatus = ShipmentStatus.IN_TRANSIT
)
