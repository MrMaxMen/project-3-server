package com.project_3.server.service

import com.project_3.server.dto.ProductDTO1
import com.project_3.server.dto.ProductGroupDTO
import com.project_3.server.dto.SellerToStockInboundDTO
import com.project_3.server.exceptions.*
import com.project_3.server.models.Product
import com.project_3.server.models.ProductGroup
import com.project_3.server.repos.CategoryRepository
import com.project_3.server.repos.ProductRepository
import com.project_3.server.repos.ProductGroupRepository
import com.project_3.server.repos.SellerRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductManagementService(
    private val productGroupRepository: ProductGroupRepository,
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val sellerRepository: SellerRepository
) {

    @Transactional
    fun addProductGroup(newProductGroupDTO: ProductGroupDTO) {

        if (newProductGroupDTO.products.isEmpty()) throw ProductGroupCreationErrorException()

        val seller =
                sellerRepository.findByIdOrNull(newProductGroupDTO.sellerId)
                        ?: throw SellerNotFoundByIdException(newProductGroupDTO.sellerId)

        val category =
                categoryRepository.findByIdOrNull(newProductGroupDTO.categoryId)
                        ?: throw CategoryNotFoundByIdException(newProductGroupDTO.categoryId)

        val newProductGroup = ProductGroup(name = newProductGroupDTO.name, seller = seller, category = category)

        val productGroups =
                newProductGroupDTO
                        .products
                        .map {
                            Product(
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
                                    productGroup = newProductGroup,
                                    weightGrams = it.weightGrams,
                                    lengthMm = it.lengthMm,
                                    widthMm = it.widthMm,
                                    heightMm = it.heightMm,
                            )
                        }
                        .toMutableSet()

        newProductGroup.products = productGroups

        productGroupRepository.save(newProductGroup)
    }

    @Transactional
    fun addProduct(productId: Long, newProduct: ProductDTO1) {

        val productGroup =
                productGroupRepository.findByIdOrNull(productId)
                        ?: throw ProductGroupNotFoundByIdException(productId)

        productGroup.products.add(
                Product(
                        name = newProduct.name,
                        description = newProduct.description,
                        mediaURLs = newProduct.mediaURLs,
                        basePrice = newProduct.basePrice,
                        discount = newProduct.discount,
                        currentPrice = newProduct.currentPrice,
                        rating = null,
                        reviewCount = null,
                        seller = productGroup.seller,
                        category = productGroup.category,
                        productGroup = productGroup,
                        weightGrams = newProduct.weightGrams,
                        lengthMm = newProduct.lengthMm,
                        widthMm = newProduct.widthMm,
                        heightMm = newProduct.heightMm,
                )
        )

        productGroupRepository.save(productGroup)
    }

    @Transactional
    fun deleteProductGroup(idProductGroupToDelete: Long) {
        if (!productGroupRepository.existsById(idProductGroupToDelete)) {
            throw ProductGroupNotFoundByIdException(idProductGroupToDelete)
        }

        productGroupRepository.deleteById(idProductGroupToDelete)
    }

    @Transactional
    fun deleteProduct(idProductToDelete: Long) {

        val product =
                productRepository.findByIdOrNull(idProductToDelete)
                        ?: throw ProductNotFoundByIdException(idProductToDelete)

        val productGroup = product.productGroup

        productGroup.products.remove(product)

        if (productGroup.products.isEmpty()) {
            productGroupRepository.deleteById(product.id!!)
        }
    }

    @Transactional
    fun modifyProductGroup(id: Long, modifiedProduct: ProductDTO1) {

        val product = productRepository.findByIdOrNull(id) ?: throw ProductNotFoundByIdException(id)

        product.name = modifiedProduct.name
        product.description = modifiedProduct.description
        product.mediaURLs = modifiedProduct.mediaURLs
        product.basePrice = modifiedProduct.basePrice
        product.discount = modifiedProduct.discount
        product.currentPrice = modifiedProduct.currentPrice

        productRepository.save(product)
    }

    @Transactional
    fun modifyProductGroup(id: Long, modifiedProductGroup: ProductGroupDTO) {

        val productGroup = productGroupRepository.findByIdOrNull(id) ?: throw ProductGroupNotFoundByIdException(id)

        productGroup.name = modifiedProductGroup.name

        productGroupRepository.save(productGroup)
    }

    fun getTotalStockQuantity(id: Long): Int {
        val product =
                productRepository.findByIdOrNull(id)
                        ?: throw ProductNotFoundByIdException(id)

        val totalQuantity = product.productOnStocks.sumOf { it.availableQuantity }
        return totalQuantity
    }

    @Transactional
    fun createInboundRequest(id: Long , inboundRequestDTO : SellerToStockInboundDTO) {

        val product =
                productRepository.findByIdOrNull(id)
                        ?: throw ProductNotFoundByIdException(id)

        // Implementation of inbound request creation logic goes here

    }
}
