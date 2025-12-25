package com.project_3.server.models

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Seller(
        var rating: Double? = null,
        @OneToMany(mappedBy = "seller", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        var itemsList: MutableSet<Item> = mutableSetOf(),
        @OneToMany(mappedBy = "seller", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        var productList: MutableSet<Product> = mutableSetOf(),
        var phoneNumber: String? = null, // сделать
        var createdAt: LocalDateTime? = null, // сделать
        var updatedAt: LocalDateTime? = null, // сделать
        var original: Boolean = false,
        name: String,
        email: String,
        password: String
) : User(name = name, email = email, password = password)
