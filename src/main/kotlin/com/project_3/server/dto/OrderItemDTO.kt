package com.project_3.server.dto


data class OrderItemDTO (

    var itemId : Long,

    var quantity : Int,

    var priceAtPurchase: Double
)