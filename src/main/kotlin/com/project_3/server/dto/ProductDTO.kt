package com.project_3.server.dto

data class ProductDTO(

    var brand: String,

    var items: MutableList<ItemDTO1> = mutableListOf(),

    var categoryId : Long,

    var sellerId: Long

    )