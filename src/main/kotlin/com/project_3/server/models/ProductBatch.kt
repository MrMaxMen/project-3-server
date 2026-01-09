package com.project_3.server.models

import com.project_3.server.models.logistics.transportation.Supply
import com.project_3.server.models.logistics.transportation.Transfer
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne


@Entity
class ProductBatch (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product,

    var quantity: Int,

    // ✅ Обратная ссылка на Supply
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supply_id")
    var supply: Supply? = null,

    // ✅ Обратная ссылка на Transfer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transfer_id")
    var transfer: Transfer? = null

)