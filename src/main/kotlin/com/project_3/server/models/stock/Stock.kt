package com.project_3.server.models.stock

import com.project_3.server.models.logistics.Orthodrome
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class Stock(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var name: String,

    var address: String,

    var latitude: Double,
    var longitude: Double,

    @OneToMany(mappedBy = "stock", cascade = [CascadeType.ALL], orphanRemoval = true)
    val orthodromes: MutableList<Orthodrome> = mutableListOf(),

    @OneToMany(mappedBy = "stock", cascade = [CascadeType.ALL], orphanRemoval = true)
    val productOnStocks: MutableSet<ProductOnStock> = mutableSetOf()
)