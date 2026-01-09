package com.project_3.server.models.logistics

import com.project_3.server.models.ProductBatch
import com.project_3.server.models.enums.TransferRequestStatus
import com.project_3.server.models.logistics.transportation.Transfer
import com.project_3.server.models.stock.Stock
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class TransferRequest(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
        @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(nullable = false) val sourceStock: Stock,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(nullable = false)
        val destinationStock: Stock,
        @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
        @JoinColumn(name = "product_batch_id", nullable = false)
        val productBatch: ProductBatch,
        @Enumerated(EnumType.STRING)
        var status: TransferRequestStatus = TransferRequestStatus.PENDING,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "transfer_id")
        var transfer: Transfer? = null,
        val createdAt: LocalDateTime = LocalDateTime.now()
)
