package com.project_3.server.models

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class Seller(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var name: String,
    var rating: Double? = null,


    @OneToMany(mappedBy = "seller", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var itemsList : MutableList <Item> = mutableListOf(),


    var email : String,
    var password: String,
    var phoneNumber : String? = null, // сделать
    var createdAt : LocalDateTime? = null, // сделать
    var updatedAt : LocalDateTime? = null, // сделать
    var original : Boolean = false

)