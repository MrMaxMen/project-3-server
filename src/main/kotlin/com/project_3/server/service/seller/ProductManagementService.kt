package com.project_3.server.service.seller

import com.project_3.server.models.Product
import com.project_3.server.repos.CategoryRepository
import com.project_3.server.repos.ItemRepository
import com.project_3.server.repos.ProductRepository
import com.project_3.server.security.JwtService
import org.springframework.stereotype.Service


@Service
class ProductManagementService (
    private val jwtService: JwtService,
    private val productRepository: ProductRepository,
    private val itemRepository: ItemRepository,
    private val categoryRepository: CategoryRepository
){

    fun addProduct(newProduct : Product){






    }

}