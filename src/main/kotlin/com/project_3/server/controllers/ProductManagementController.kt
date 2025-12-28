package com.project_3.server.controllers

import com.project_3.server.dto.ProductDTO1
import com.project_3.server.dto.ProductGroupDTO
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
        @RequestBody newProduct : ProductGroupDTO
    ): ResponseEntity<String> {

        productManagementService.addProductGroup(newProduct)

        return ResponseEntity.ok("ProductGroup added successfully")
    }

    @PostMapping("/{id}/products/add")
    fun addProductItem(
        @RequestBody newItem : ProductDTO1,
        @PathVariable id: Long
    ) : ResponseEntity<String> {

        productManagementService.addProduct(productId = id ,newProduct = newItem)

        return ResponseEntity.ok("Product added successfully")
    }

    @DeleteMapping("/{id}/delete")
    fun deleteProduct(
        @PathVariable id : Long
    ) : ResponseEntity<String> {

        productManagementService.deleteProductGroup(idProductGroupToDelete = id)

        return ResponseEntity.ok("productGroup deleted successfully")
    }


    @DeleteMapping("/products/{id}/delete")
    fun deleteItem(
        @PathVariable id : Long
    ) : ResponseEntity<String> {

        productManagementService.deleteProduct(idProductToDelete =  id)

        return ResponseEntity.ok("product deleted successfully")
    }

    @PutMapping("/products/{id}/modify")
    fun modifyItem(
        @RequestBody modifiedItem : ProductDTO1,
        @PathVariable id : Long
    ) : ResponseEntity<String> {

        productManagementService.modifyProductGroup(id,modifiedItem)

        return ResponseEntity.ok("product modified successfully")
    }


    @PutMapping("/{id}/modify")
    fun modifyProduct(
        @RequestBody modifiedProduct : ProductGroupDTO,
        @PathVariable id : Long
    ) : ResponseEntity<String> {

        productManagementService.modifyProductGroup(id,modifiedProduct)

        return ResponseEntity.ok("productGroup modified successfully")
    }




}