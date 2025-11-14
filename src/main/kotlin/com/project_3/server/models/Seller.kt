package com.project_3.server.models

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Seller(


    var rating: Double? = null,


    @OneToMany(mappedBy = "seller", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var itemsList : MutableList <Item> = mutableListOf(),


    @OneToMany(mappedBy = "seller", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var productList : MutableList <Product> = mutableListOf(),


    var phoneNumber : String? = null, // сделать
    var createdAt : LocalDateTime? = null, // сделать
    var updatedAt : LocalDateTime? = null, // сделать
    var original : Boolean = false,

    name : String,
    email : String,
    password : String

) : User(
    name = name,
    email = email,
    password = password
)