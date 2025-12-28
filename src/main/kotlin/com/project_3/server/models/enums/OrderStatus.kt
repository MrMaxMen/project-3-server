package com.project_3.server.models.enums

enum class OrderStatus {
    IN_TRANSIT,   // заказ в пути
    ARRIVED,      // заказ прибыл в пункт выдачи
    COMPLETED     // заказ забран
}