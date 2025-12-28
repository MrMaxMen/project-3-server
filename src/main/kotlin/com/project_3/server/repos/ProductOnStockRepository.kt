package com.project_3.server.repos

import com.project_3.server.models.Product
import com.project_3.server.models.stock.Stock
import com.project_3.server.models.stock.ProductOnStock
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository interface ProductOnStockRepository : JpaRepository<ProductOnStock, Long>{
    fun findByProduct(product: Product): List<ProductOnStock>
    fun findByProductAndStock(product : Product, stock: Stock): ProductOnStock

    @Modifying
    @Query("UPDATE ProductOnStock p SET p.availableQuantity = p.availableQuantity - :quantity WHERE p.id = :id AND p.availableQuantity >= :quantity")
    fun reduceStock(id: Long, quantity: Int): Int
}
