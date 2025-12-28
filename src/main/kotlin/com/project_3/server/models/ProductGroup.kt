package com.project_3.server.models

import com.fasterxml.jackson.annotation.JsonManagedReference
import com.project_3.server.models.users.Seller
import jakarta.persistence.*

@Entity
class ProductGroup(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var name: String,

    @OneToMany(mappedBy = "productGroup", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    var products: MutableSet<Product> = mutableSetOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    var category: Category,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    var seller: Seller,
)
