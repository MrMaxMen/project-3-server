package com.project_3.server.dto

data class ProductGroupDTO(
        var name: String,
        var items: MutableSet<ProductDTO1> = mutableSetOf(),
        var categoryId: Long,
        var sellerId: Long
)
