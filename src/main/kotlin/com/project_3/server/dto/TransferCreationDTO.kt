package com.project_3.server.dto

data class TransferCreationDTO(

    val sourceStockId: Long,
    val destinationStockId: Long,

    val productBatchList: List<ProductBatchDTO>

)