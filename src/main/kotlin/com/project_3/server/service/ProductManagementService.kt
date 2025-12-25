package com.project_3.server.service

import com.project_3.server.dto.ItemDTO1
import com.project_3.server.dto.ProductDTO
import com.project_3.server.exceptions.*
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
class ProductManagementService(
        private val productRepository: ProductRepository,
        private val itemRepository: ItemRepository,
        private val categoryRepository: CategoryRepository,
        private val sellerRepository: SellerRepository
) {

    @Transactional
    fun addProduct(newProductDTO: ProductDTO) {

        if (newProductDTO.items.isEmpty()) throw ProductCreationErrorException()

        val seller =
                sellerRepository.findByIdOrNull(newProductDTO.sellerId)
                        ?: throw SellerNotFoundByIdException(newProductDTO.sellerId)

        val category =
                categoryRepository.findByIdOrNull(newProductDTO.categoryId)
                        ?: throw CategoryNotFoundByIdException(newProductDTO.categoryId)

        val newProduct = Product(brand = newProductDTO.brand, seller = seller, category = category)

        val items =
                newProductDTO
                        .items
                        .map {
                            Item(
                                    name = it.name,
                                    description = it.description,
                                    mediaURLs = it.mediaURLs,
                                    basePrice = it.basePrice,
                                    discount = it.discount,
                                    currentPrice = it.currentPrice,
                                    rating = null,
                                    reviewCount = null,
                                    seller = seller,
                                    category = category,
                                    product = newProduct
                            )
                        }
                        .toMutableSet()

        newProduct.items = items

        productRepository.save(newProduct)
    }

    @Transactional
    fun addItem(productId: Long, newItem: ItemDTO1) {

        val product =
                productRepository.findByIdOrNull(productId)
                        ?: throw ProductNotFoundByIdException(productId)

        product.items.add(
                Item(
                        name = newItem.name,
                        description = newItem.description,
                        mediaURLs = newItem.mediaURLs,
                        basePrice = newItem.basePrice,
                        discount = newItem.discount,
                        currentPrice = newItem.currentPrice,
                        rating = null,
                        reviewCount = null,
                        seller = product.seller,
                        category = product.category,
                        product = product,
                )
        )

        productRepository.save(product)
    }

    @Transactional
    fun deleteProduct(idProductToDelete: Long) {
        if (!productRepository.existsById(idProductToDelete)) {
            throw ProductNotFoundByIdException(idProductToDelete)
        }

        productRepository.deleteById(idProductToDelete)
    }

    @Transactional
    fun deleteItem(idItemToDelete: Long) {

        val item =
                itemRepository.findByIdOrNull(idItemToDelete)
                        ?: throw ItemNotFoundByIdException(idItemToDelete)

        val product = item.product!!

        product.items.remove(item)

        if (product.items.isEmpty()) {
            productRepository.deleteById(product.id!!)
        }
    }

    @Transactional
    fun modifyItem(id: Long, modifiedItem: ItemDTO1) {

        val item = itemRepository.findByIdOrNull(id) ?: throw ItemNotFoundByIdException(id)

        item.name = modifiedItem.name
        item.description = modifiedItem.description
        item.mediaURLs = modifiedItem.mediaURLs
        item.basePrice = modifiedItem.basePrice
        item.discount = modifiedItem.discount
        item.currentPrice = modifiedItem.currentPrice

        itemRepository.save(item)
    }

    @Transactional
    fun modifyProduct(id: Long, modifiedProduct: ProductDTO) {

        val product = productRepository.findByIdOrNull(id) ?: throw ProductNotFoundByIdException(id)

        product.brand = modifiedProduct.brand

        productRepository.save(product)
    }
}
