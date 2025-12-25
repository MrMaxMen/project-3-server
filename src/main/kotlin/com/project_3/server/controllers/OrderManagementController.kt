package com.project_3.server.controllers

import com.project_3.server.dto.OrderDTO
import com.project_3.server.service.OrderManagementService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/main/orders")
class OrderManagementController (
    private val orderManagementService: OrderManagementService
){


    @PreAuthorize("hasRole('BUYER')")
    @PostMapping("/create")
    fun orderCreation(
        @RequestBody newOrderDTO : OrderDTO,
        authentication: Authentication
    ) : ResponseEntity<String> {

        val buyerId = authentication.name.toLong()

        orderManagementService.createOrder(buyerId,newOrderDTO)

        return ResponseEntity.ok("Order creation successfully")
    }

}