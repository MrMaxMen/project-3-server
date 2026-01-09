package com.project_3.server.models.logistics.transportation

import com.project_3.server.models.enums.TransportationStatus
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
abstract class Transportation(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @Enumerated(EnumType.STRING)
        var status: TransportationStatus = TransportationStatus.WAITING,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn
        var vehicle: Vehicle? = null,

        var departureTime: java.time.LocalDateTime? = null
)
