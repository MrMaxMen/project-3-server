package com.project_3.server.models.logistics.transportation

import com.project_3.server.models.enums.TransportationStatus
import com.project_3.server.models.ProductBatch
import com.project_3.server.models.stock.Stock
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany

@Entity
class Transfer(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    var sourceStock: Stock,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    var destinationStock: Stock,

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "transfer")
    var productBatchList: MutableList<ProductBatch> = mutableListOf(),

    vehicle: Vehicle,
    status: TransportationStatus
) : Transportation(vehicle = vehicle, status = status)