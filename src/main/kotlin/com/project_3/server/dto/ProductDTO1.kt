package com.project_3.server.dto

data class ProductDTO1(

    var name: String,
    var description: String,
    var mediaURLs : MutableList<String> = mutableListOf(),
    var basePrice: Double,
    var stock : Int,
    var discount : Double? = null,
    var currentPrice : Double,
    var quantity : Int

)