package com.project_3.server.models.delivery

import com.project_3.server.models.stock.Stock
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime

@Entity
class Orthodrome (

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn( nullable = false)
    val stock: Stock,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn( nullable = false)
    val pickupPoint: PickupPoint,

    @Column(nullable = false)
    val distanceKm: Double,

    @Column(nullable = false)
    val calculatedAt: LocalDateTime = LocalDateTime.now()

)