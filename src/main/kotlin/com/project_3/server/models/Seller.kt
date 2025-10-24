package com.project_3.server.models

import jakarta.persistence.*

@Entity
data class Seller(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var name: String,
    var rating: Double? = null,


    @OneToMany(mappedBy = "seller", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var itemsList : MutableList <Item> = mutableListOf()

)