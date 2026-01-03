package com.project_3.server.dto


data class SellerToStockInboundDTO(

    var sourceAddress: String,

    var productBatchList: MutableList<ProductBatchDTO> = mutableListOf(),

)