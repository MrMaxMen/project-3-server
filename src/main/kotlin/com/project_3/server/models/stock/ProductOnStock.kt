package com.project_3.server.models.stock

import com.project_3.server.models.Product
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class ProductOnStock(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id")
    val stock: Stock,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    val product: Product,

    var realQuantity: Int,
    var availableQuantity: Int,
)