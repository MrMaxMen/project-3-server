package com.project_3.server.models

import jakarta.persistence.*

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
    val stockItems: MutableList<StockItem> = mutableListOf()
)
