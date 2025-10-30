package com.project_3.server.controllers.seller

import com.project_3.server.models.Product
import com.project_3.server.service.seller.ProductManagementService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/seller/products")
class ProductManagementController (
    private val productManagementService: ProductManagementService
){

    @PostMapping("/add")
    fun addProduct(@RequestBody newProduct : Product) : ResponseEntity<String>{
        productManagementService.addProduct(newProduct)
        return ResponseEntity.ok("Product added successfully")
    }


}