package com.project_3.server.dto

data class ProductDTO(

    var brand: String,

    var items: MutableList<ItemDTO> = mutableListOf(),

    var category : CategoryDTO,

    var seller: SellerDTO,

)