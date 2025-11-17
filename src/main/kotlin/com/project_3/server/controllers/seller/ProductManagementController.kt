package com.project_3.server.controllers.seller


import com.project_3.server.InvalidTokenException
import com.project_3.server.NoTokenException
import com.project_3.server.dto.ItemDTO1
import com.project_3.server.dto.ProductDTO
import com.project_3.server.security.JwtService
import com.project_3.server.service.seller.ProductManagementService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
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

    @PostMapping("/{id}/items/add")
    fun addProductItem(
        @RequestHeader("Authorization") header: String?,
        @RequestBody newItem : ItemDTO1,
        @PathVariable id: Long
    ) : ResponseEntity<String> {
        val token: String = jwtService.extractToken(header) ?: throw NoTokenException()
        if(!jwtService.validateToken(token)){
            throw InvalidTokenException()
        }

        productManagementService.addItem(productId = id ,newItem = newItem)

        return ResponseEntity.ok("Item added successfully")
    }

    @DeleteMapping("/{id}/delete")
    fun deleteProduct(
        @RequestHeader("Authorization") header: String?,
        @PathVariable id : Long
    ) : ResponseEntity<String> {
        val token: String = jwtService.extractToken(header) ?: throw NoTokenException()
        if(!jwtService.validateToken(token)){
            throw InvalidTokenException()
        }

        productManagementService.deleteProduct(idProductToDelete = id)

        return ResponseEntity.ok("product deleted successfully")
    }


    @DeleteMapping("/items/{id}/delete")
    fun deleteItem(
        @RequestHeader("Authorization") header: String?,
        @PathVariable id : Long
    ) : ResponseEntity<String> {
        val token: String = jwtService.extractToken(header) ?: throw NoTokenException()
        if(!jwtService.validateToken(token)){
            throw InvalidTokenException()
        }

        productManagementService.deleteItem(idItemToDelete =  id)

        return ResponseEntity.ok("item deleted successfully")
    }

    @PutMapping("/items/{id}/modify")
    fun modifyItem(
        @RequestHeader("Authorization") header: String?,
        @RequestBody modifiedItem : ItemDTO1,
        @PathVariable id : Long
    ) : ResponseEntity<String> {
        val token: String = jwtService.extractToken(header) ?: throw NoTokenException()
        if(!jwtService.validateToken(token)){
            throw InvalidTokenException()
        }

        productManagementService.modifyItem(id,modifiedItem)

        return ResponseEntity.ok("item modified successfully")
    }


    @PutMapping("/{id}/modify")
    fun modifyProduct(
        @RequestHeader("Authorization") header: String?,
        @RequestBody modifiedProduct : ProductDTO,
        @PathVariable id : Long
    ) : ResponseEntity<String> {
        val token: String = jwtService.extractToken(header) ?: throw NoTokenException()
        if(!jwtService.validateToken(token)){
            throw InvalidTokenException()
        }

        productManagementService.modifyProduct(id,modifiedProduct)

        return ResponseEntity.ok("product modified successfully")
    }




}

