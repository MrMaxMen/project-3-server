package com.project_3.server.models.enums

enum class TransferRequestStatus {
    PENDING, // Ожидает назначения в рейс
    ASSIGNED, // Назначен в Transfer
    IN_TRANSIT, // В пути
    COMPLETED // Доставлен
}
