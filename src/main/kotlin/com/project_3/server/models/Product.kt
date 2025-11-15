package com.project_3.server.models

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*

@Entity
class Product(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var brand: String,

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], orphanRemoval = true,fetch = FetchType.LAZY)
    @JsonManagedReference
    var items: MutableList<Item> = mutableListOf(),


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    var category : Category,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    var seller: Seller,

)