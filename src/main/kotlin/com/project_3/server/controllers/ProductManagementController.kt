package com.project_3.server.controllers

import com.project_3.server.dto.ProductDTO1
import com.project_3.server.dto.ProductGroupDTO
import com.project_3.server.dto.SellerToStockInboundDTO
import com.project_3.server.service.ProductManagementService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@PreAuthorize("hasRole('SELLER')")
@RequestMapping("/api/seller/productGroups")
class ProductManagementController (
    private val productManagementService: ProductManagementService
){

    @PostMapping("/add")
    fun addProductGroup(
        @RequestBody newProductGroup : ProductGroupDTO
    ): ResponseEntity<String> {

        productManagementService.addProductGroup(newProductGroup)

        return ResponseEntity.ok("ProductGroup added successfully")
    }

    @PostMapping("/{id}/products/add")
    fun addProduct(
        @RequestBody newProduct : ProductDTO1,
        @PathVariable id: Long
    ) : ResponseEntity<String> {

        productManagementService.addProduct(productId = id ,newProduct = newProduct)

        return ResponseEntity.ok("Product added successfully")
    }

    @DeleteMapping("/{id}/delete")
    fun deleteProductGroup(
        @PathVariable id : Long
    ) : ResponseEntity<String> {

        productManagementService.deleteProductGroup(idProductGroupToDelete = id)

        return ResponseEntity.ok("productGroup deleted successfully")
    }


    @DeleteMapping("/products/{id}/delete")
    fun deleteProduct(
        @PathVariable id : Long
    ) : ResponseEntity<String> {

        productManagementService.deleteProduct(idProductToDelete =  id)

        return ResponseEntity.ok("product deleted successfully")
    }

    @PutMapping("/products/{id}/modify")
    fun modifyProduct(
        @RequestBody modifiedProduct : ProductDTO1,
        @PathVariable id : Long
    ) : ResponseEntity<String> {

        productManagementService.modifyProductGroup(id, modifiedProduct)

        return ResponseEntity.ok("product modified successfully")
    }


    @PutMapping("/{id}/modify")
    fun modifyProductGroup(
        @RequestBody modifiedProductGroup : ProductGroupDTO,
        @PathVariable id : Long
    ) : ResponseEntity<String> {

        productManagementService.modifyProductGroup(id, modifiedProductGroup)

        return ResponseEntity.ok("productGroup modified successfully")
    }


    @GetMapping("/product/{id}/getTotalStockQuantity")
    fun getTotalStockQuantity(
        @PathVariable id : Long
    ) : ResponseEntity<Int> {
        val totalQuantity = productManagementService.getTotalStockQuantity(id)
        return ResponseEntity.ok(totalQuantity)
    }


    @PostMapping("/product/{id}/inboundRequest")
    fun createInboundRequest(
        @PathVariable id : Long,
        @RequestBody inboundRequestDTO : SellerToStockInboundDTO
    ) : ResponseEntity<String> {

        productManagementService.createInboundRequest(id, inboundRequestDTO)

        return ResponseEntity.ok("Inbound request created successfully")
    }
}