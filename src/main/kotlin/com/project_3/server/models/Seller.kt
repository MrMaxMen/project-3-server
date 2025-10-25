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
    var phoneNumber : String,
    var createdAt : LocalDateTime,
    var updatedAt : LocalDateTime,
    var original : Boolean = false

)