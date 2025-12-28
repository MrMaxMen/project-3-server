package com.project_3.server.repos

import com.project_3.server.models.Item
import com.project_3.server.models.Stock
import com.project_3.server.models.StockItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository interface StockItemRepository : JpaRepository<StockItem, Long>{
    fun findByItem(item: Item): List<StockItem>
    fun findByItemAndStock(item : Item,stock: Stock): StockItem
}
