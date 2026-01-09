package com.project_3.server.repos

import com.project_3.server.models.enums.TransferRequestStatus
import com.project_3.server.models.logistics.TransferRequest
import com.project_3.server.models.stock.Stock
import org.springframework.data.jpa.repository.JpaRepository

interface TransferRequestRepository : JpaRepository<TransferRequest, Long> {

    fun findAllByStatus(status: TransferRequestStatus): List<TransferRequest>

    fun findAllByStatusAndTransferIsNull(status: TransferRequestStatus): List<TransferRequest>

    fun findAllByStatusAndSourceStockAndDestinationStock(
            status: TransferRequestStatus,
            sourceStock: Stock,
            destinationStock: Stock
    ): List<TransferRequest>
}
