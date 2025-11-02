package com.project_3.server.controllers.seller


import com.project_3.server.InvalidTokenException
import com.project_3.server.NoTokenException
import com.project_3.server.dto.ProductDTO
import com.project_3.server.security.JwtService
import com.project_3.server.service.seller.ProductManagementService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/seller/products")
class ProductManagementController (
    private val productManagementService: ProductManagementService,
    private val jwtService: JwtService
){

    @PostMapping("/add")
    fun addProduct(
        @RequestHeader("Authorization") header: String?,
        @RequestBody newProduct : ProductDTO
    ): ResponseEntity<String>{

        val token: String = jwtService.extractToken(header) ?: throw NoTokenException()

        if(!jwtService.validateToken(token)){
            throw InvalidTokenException()
        }

        productManagementService.addProduct(newProduct)

        return ResponseEntity.ok("Product added successfully")
    }


}