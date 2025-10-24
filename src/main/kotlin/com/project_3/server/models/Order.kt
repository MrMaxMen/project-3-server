package com.project_3.server.models

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var orderDateTime : LocalDateTime,

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true)
    var orderItems: MutableList<OrderItem> = mutableListOf(),


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn()
    var buyer: Buyer,

)