package com.project_3.server.dto


data class OrderDTO(

    var pickupPointId : Long,

    var orderProducts : MutableList<ProductInOrderDTO> = mutableListOf()

)