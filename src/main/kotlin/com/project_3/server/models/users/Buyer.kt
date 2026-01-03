package com.project_3.server.models.users

import com.project_3.server.models.order.Order
import com.project_3.server.models.Product
import com.project_3.server.models.Review
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany

@Entity
class Buyer(
    @OneToMany(mappedBy = "buyer", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        var orderList: MutableSet<Order> = mutableSetOf(),
    @OneToMany(mappedBy = "buyer", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        var reviewList: MutableSet<Review> = mutableSetOf(),
    @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(
                name = "buyer_favorites",
                joinColumns = [JoinColumn(name = "buyer_id")],
                inverseJoinColumns = [JoinColumn(name = "item_id")]
        )
        var favorites: MutableSet<Product> = mutableSetOf(),

        // корзина

    name: String,
    email: String,
    password: String
) : User(name = name, email = email, password = password)