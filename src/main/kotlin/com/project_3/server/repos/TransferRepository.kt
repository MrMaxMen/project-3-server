package com.project_3.server.repos

import com.project_3.server.models.enums.TransportationStatus
import com.project_3.server.models.logistics.transportation.Transfer
import com.project_3.server.models.stock.Stock
import org.springframework.data.jpa.repository.JpaRepository

interface TransferRepository : JpaRepository<Transfer, Long> {

    fun findAllByStatus(status: TransportationStatus): List<Transfer>

    fun findAllBySourceStockAndDestinationStockAndStatus(
            sourceStock: Stock,
            destinationStock: Stock,
            status: TransportationStatus
    ): List<Transfer>
}
