package com.project_3.server.models

import jakarta.persistence.*
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