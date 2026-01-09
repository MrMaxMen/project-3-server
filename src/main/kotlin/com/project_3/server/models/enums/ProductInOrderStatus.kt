package com.project_3.server.models.enums

enum class ProductInOrderStatus {
    PENDING,
    WAITING_FOR_TRANSPORT,
    IN_TRANSIT,
    DELIVERED,
    CANCELLED
}
