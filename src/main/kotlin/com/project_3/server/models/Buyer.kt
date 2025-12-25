package com.project_3.server.models

import jakarta.persistence.*
import kotlin.String

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
        var favorites: MutableSet<Item> = mutableSetOf(),

        // корзина

        name: String,
        email: String,
        password: String
) : User(name = name, email = email, password = password)
