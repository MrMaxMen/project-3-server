package com.project_3.server.controllers.main

import com.project_3.server.InvalidTokenException
import com.project_3.server.NoTokenException
import com.project_3.server.dto.OrderDTO
import com.project_3.server.dto.ProductDTO
import com.project_3.server.security.JwtService
import com.project_3.server.service.main.OrderManagementService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/main/orders")
class OrderManagementController (
    private val orderManagementService: OrderManagementService,
    private val jwtService: JwtService
){

    @PutMapping("/{buyerId}/create")
    fun orderCreation(
        @RequestHeader("Authorization") header: String?,
        @RequestBody newOrderDTO : OrderDTO,
        @PathVariable buyerId : Long
    ) : ResponseEntity<String> {

        val token: String = jwtService.extractToken(header) ?: throw NoTokenException()
        if(!jwtService.validateToken(token)){
            throw InvalidTokenException()
        }

        orderManagementService.createOrder(buyerId,newOrderDTO)

        return ResponseEntity.ok("Order creation successfully")
    }

}