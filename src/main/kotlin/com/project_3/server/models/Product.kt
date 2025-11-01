package com.project_3.server.models

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*

@Entity
data class Product(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val brand: String? = null,

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JsonManagedReference
    val items: MutableList<Item> = mutableListOf(),


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    var category : Category,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    var seller: Seller,

)