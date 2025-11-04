package com.project_3.server.dto

data class ItemDTO2 (

    var productId: Long,

    var name: String,
    var description: String,
    var mediaURLs : MutableList<String> = mutableListOf(),
    var basePrice: Double,
    var stock : Int,
    var discount : Double? = null,
    var currentPrice : Double,

    )