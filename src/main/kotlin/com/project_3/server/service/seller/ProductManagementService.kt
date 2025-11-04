package com.project_3.server.service.seller

import com.project_3.server.CategoryNotFoundException
import com.project_3.server.ProductCreationErrorException
import com.project_3.server.ProductNotFoundException
import com.project_3.server.SellerNotFoundException
import com.project_3.server.dto.ItemDTO2
import com.project_3.server.dto.ProductDTO
import com.project_3.server.models.Item
import com.project_3.server.models.Product
import com.project_3.server.repos.CategoryRepository
import com.project_3.server.repos.ItemRepository
import com.project_3.server.repos.ProductRepository
import com.project_3.server.repos.SellerRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service


@Service
class ProductManagementService (
    private val productRepository: ProductRepository,
    private val itemRepository: ItemRepository,
    private val categoryRepository: CategoryRepository,
    private val sellerRepository: SellerRepository
){

    fun addProduct(newProductDTO : ProductDTO){

        if(newProductDTO.items.isEmpty()) throw ProductCreationErrorException()

        val seller = sellerRepository.findByIdOrNull(newProductDTO.sellerId) ?: throw SellerNotFoundException()

        val category = categoryRepository.findByIdOrNull(newProductDTO.categoryId) ?: throw CategoryNotFoundException()

        val newProduct = Product(
            brand = newProductDTO.brand,
            seller = seller,
            category = category
        )

        val items = newProductDTO.items.map{
            Item(
                name = it.name,
                description = it.description,
                mediaURLs = it.mediaURLs,
                basePrice = it.basePrice,
                stock = it.stock,
                discount = it.discount,
                currentPrice = it.currentPrice,
                rating = null,
                reviewCount = null,
                seller = seller,
                category = category,
                product =  newProduct
            )
        }.toMutableList()

        newProduct.items = items

        productRepository.save(newProduct)
    }


    fun addItem(newItem : ItemDTO2){

        val product = productRepository.findByIdOrNull(newItem.productId) ?: throw ProductNotFoundException()

        product.items.add(Item(
            name = newItem.name,
            description = newItem.description,
            mediaURLs = newItem.mediaURLs,
            basePrice = newItem.basePrice,
            stock = newItem.stock,
            discount = newItem.discount,
            currentPrice = newItem.currentPrice,
            rating = null,
            reviewCount = null,
            seller = product.seller,
            category = product.category,
            product =  product
        ))

        productRepository.save(product)

    }


}