package com.project_3.server.models

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "\"order\"")
class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var orderDateTime : LocalDateTime,

    var arrivalDateTime: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    var pickupPoint: PickupPoint,


    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true)
    var orderItems: MutableList<OrderItem> = mutableListOf(),


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    var buyer: Buyer,

    @Enumerated(EnumType.STRING)
    var status: OrderStatus = OrderStatus.IN_TRANSIT

)

enum class OrderStatus {
    IN_TRANSIT,   // заказ в пути
    ARRIVED,      // заказ прибыл в пункт выдачи
    COMPLETED     // заказ забран
}
