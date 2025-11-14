package com.project_3.server.models

import jakarta.persistence.*
import kotlin.String

@Entity
class Buyer(


    @OneToMany(mappedBy = "buyer", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var orderList : MutableList<Order> = mutableListOf(),

    @OneToMany(mappedBy = "buyer", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var reviewList : MutableList <Review> = mutableListOf(),


    name : String,
    email : String,
    password : String

) : User(
    name = name,
    email = email,
    password = password
)