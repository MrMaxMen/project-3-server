package com.project_3.server.models

import jakarta.persistence.*

@Entity
data class Buyer(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var name: String,
    var email: String,


    @OneToMany(mappedBy = "buyer", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var orderList : MutableList<Order> = mutableListOf(),

    @OneToMany(mappedBy = "buyer", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var reviewList : MutableList <Review> = mutableListOf()

)