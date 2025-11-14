package com.project_3.server.service.seller

import com.project_3.server.CategoryNotFoundException
import com.project_3.server.ItemNotFoundException
import com.project_3.server.ProductCreationErrorException
import com.project_3.server.ProductNotFoundException
import com.project_3.server.SellerNotFoundException
import com.project_3.server.dto.ItemDTO1
import com.project_3.server.dto.ProductDTO
import com.project_3.server.models.Item
import com.project_3.server.models.Product
import com.project_3.server.repos.CategoryRepository
import com.project_3.server.repos.ItemRepository
import com.project_3.server.repos.ProductRepository
import com.project_3.server.repos.SellerRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class ProductManagementService (
    private val productRepository: ProductRepository,
    private val itemRepository: ItemRepository,
    private val categoryRepository: CategoryRepository,
    private val sellerRepository: SellerRepository
){

    @Transactional
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


    @Transactional
    fun addItem(productId : Long,newItem : ItemDTO1){

        val product = productRepository.findByIdOrNull(productId) ?: throw ProductNotFoundException(productId)

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


    @Transactional
    fun deleteProduct(idProductToDelete : Long) {
        if (!productRepository.existsById(idProductToDelete)) {
            throw ProductNotFoundException(idProductToDelete)
        }

        productRepository.deleteById(idProductToDelete)
    }



    @Transactional
    fun deleteItem(idItemToDelete : Long) {

        val item = itemRepository.findByIdOrNull(idItemToDelete) ?: throw ItemNotFoundException(idItemToDelete)

        val product  = item.product!!

        product.items.remove(item)


        if(product.items.isEmpty()){
            productRepository.deleteById(product.id!!)
        }
    }


}