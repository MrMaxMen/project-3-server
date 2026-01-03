package com.project_3.server.repos

import com.project_3.server.models.enums.TransportationStatus
import com.project_3.server.models.logistics.transportation.Delivery
import com.project_3.server.models.stock.Stock
import org.springframework.data.jpa.repository.JpaRepository

interface DeliveryRepository : JpaRepository<Delivery, Long> {
    fun findBySourceStockAndStatus(sourceStock: Stock, status: TransportationStatus): List<Delivery>
}
