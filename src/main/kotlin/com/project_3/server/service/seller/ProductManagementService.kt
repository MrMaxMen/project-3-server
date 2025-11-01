package com.project_3.server.service.seller

import com.project_3.server.CategoryNotFoundException
import com.project_3.server.ProductCreationErrorException
import com.project_3.server.SellerNotFoundException
import com.project_3.server.models.Product
import com.project_3.server.repos.CategoryRepository
import com.project_3.server.repos.ItemRepository
import com.project_3.server.repos.ProductRepository
import com.project_3.server.repos.SellerRepository
import com.project_3.server.security.JwtService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service


@Service
class ProductManagementService (
    private val productRepository: ProductRepository,
    private val itemRepository: ItemRepository,
    private val categoryRepository: CategoryRepository,
    private val sellerRepository: SellerRepository
){

    fun addProduct(newProduct : Product){

        if(newProduct.items.isEmpty()) throw ProductCreationErrorException()

        newProduct.items.forEach { item ->
            if(item.product != newProduct){
                item.product = newProduct
            }
        }


        val seller = sellerRepository.findByIdOrNull(newProduct.seller.id!!) ?: throw SellerNotFoundException()

        newProduct.seller = seller

        val category = categoryRepository.findByIdOrNull(newProduct.category.id!!) ?: throw CategoryNotFoundException()

        newProduct.category = category

        productRepository.save(newProduct)
    }

}