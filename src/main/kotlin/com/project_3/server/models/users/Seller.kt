package com.project_3.server.models.users

import com.project_3.server.models.Product
import com.project_3.server.models.ProductGroup
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.OneToMany
import java.time.LocalDateTime

@Entity
class Seller(

    var rating: Double? = null,

    @OneToMany(mappedBy = "seller", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        var itemsList: MutableSet<Product> = mutableSetOf(),

    @OneToMany(mappedBy = "seller", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        var productGroupList: MutableSet<ProductGroup> = mutableSetOf(),

    var phoneNumber: String? = null, // сделать

    var createdAt: LocalDateTime? = null, // сделать

    var updatedAt: LocalDateTime? = null, // сделать

    var original: Boolean = false,

    name: String,
    email: String,
    password: String

) : User(name = name, email = email, password = password)