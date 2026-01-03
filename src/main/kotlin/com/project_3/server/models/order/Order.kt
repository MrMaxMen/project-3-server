package com.project_3.server.models.order

import com.project_3.server.models.logistics.PickupPoint
import com.project_3.server.models.enums.OrderStatus
import com.project_3.server.models.users.Buyer
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
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
    var productInOrders: MutableList<ProductInOrder> = mutableListOf(),


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    var buyer: Buyer,

    @Enumerated(EnumType.STRING)
    var status: OrderStatus = OrderStatus.IN_TRANSIT

)