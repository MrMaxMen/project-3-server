package com.project_3.server.dto


data class ProductInOrderDTO (

    var productId : Long,

    var quantity : Int,

    var priceAtPurchase: Double
)