package com.project_3.server.dto

data class ProductDTO(
        var brand: String,
        var items: MutableSet<ItemDTO1> = mutableSetOf(),
        var categoryId: Long,
        var sellerId: Long
)
