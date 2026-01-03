package com.project_3.server.models.logistics.transportation

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Vehicle(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    var licensePlate: String,

    var brand: String,
    var model: String,

    var capacityKg: Int,
    var volumeM3: Double,

    var isFree: Boolean = true


)