package com.project_3.server.dto

data class SupplyCreationDTO(

    val sourceAddress: String,
    val sourceLatitude: Double,
    val sourceLongitude: Double,


    val productBatchDTOList: List<ProductBatchDTO>
)