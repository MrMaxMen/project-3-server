package com.project_3.server.models.logistics.transportation

import com.project_3.server.models.enums.TransportationStatus
import com.project_3.server.models.order.ProductInOrder
import com.project_3.server.models.stock.Stock
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany

@Entity
class Delivery(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    var sourceStock: Stock,


    @OneToMany(mappedBy = "delivery")
    var productInOrderList: MutableList<ProductInOrder> = mutableListOf(),

    var waitingTimeMinutes: Int = 0,

    var currentWeightKg: Double = 0.0,
    var currentVolumeM3: Double = 0.0,


    vehicle: Vehicle? = null,
    status: TransportationStatus
) : Transportation(vehicle = vehicle, status = status)