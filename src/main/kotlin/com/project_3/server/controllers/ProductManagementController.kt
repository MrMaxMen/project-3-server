package com.project_3.server.controllers

import com.project_3.server.dto.ItemDTO1
import com.project_3.server.dto.ProductDTO
import com.project_3.server.service.ProductManagementService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@PreAuthorize("hasRole('SELLER')")
@RequestMapping("/api/seller/products")
class ProductManagementController (
    private val productManagementService: ProductManagementService
){

    @PostMapping("/add")
    fun addProduct(
        @RequestBody newProduct : ProductDTO
    ): ResponseEntity<String> {

        productManagementService.addProduct(newProduct)

        return ResponseEntity.ok("Product added successfully")
    }

    @PostMapping("/{id}/items/add")
    fun addProductItem(
        @RequestBody newItem : ItemDTO1,
        @PathVariable id: Long
    ) : ResponseEntity<String> {

        productManagementService.addItem(productId = id ,newItem = newItem)

        return ResponseEntity.ok("Item added successfully")
    }

    @DeleteMapping("/{id}/delete")
    fun deleteProduct(
        @PathVariable id : Long
    ) : ResponseEntity<String> {

        productManagementService.deleteProduct(idProductToDelete = id)

        return ResponseEntity.ok("product deleted successfully")
    }


    @DeleteMapping("/items/{id}/delete")
    fun deleteItem(
        @PathVariable id : Long
    ) : ResponseEntity<String> {

        productManagementService.deleteItem(idItemToDelete =  id)

        return ResponseEntity.ok("item deleted successfully")
    }

    @PutMapping("/items/{id}/modify")
    fun modifyItem(
        @RequestBody modifiedItem : ItemDTO1,
        @PathVariable id : Long
    ) : ResponseEntity<String> {

        productManagementService.modifyItem(id,modifiedItem)

        return ResponseEntity.ok("item modified successfully")
    }


    @PutMapping("/{id}/modify")
    fun modifyProduct(
        @RequestBody modifiedProduct : ProductDTO,
        @PathVariable id : Long
    ) : ResponseEntity<String> {

        productManagementService.modifyProduct(id,modifiedProduct)

        return ResponseEntity.ok("product modified successfully")
    }




}