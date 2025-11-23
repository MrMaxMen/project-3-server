package com.project_3.server.dto

import com.project_3.server.models.PickupPoint


data class OrderDTO(

    var pickupPointId : Long,

    var orderItems : MutableList<OrderItemDTO> = mutableListOf()

)