package com.project_3.server.models

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.*

@Entity
data class Item(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var name: String,
    var description: String,
    var mediaURLs : MutableList<String> = mutableListOf(),
    var basePrice: Double,
    var stock : Int,
    var discount : Double? = null,
    var currentPrice : Double,
    var rating : Double? = null,
    var reviewCount : Int? = null,



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    var seller: Seller,


    @OneToMany(mappedBy = "item", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var reviewList : MutableList <Review> = mutableListOf(),


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    var category : Category,

    @OneToMany(mappedBy = "item", cascade = [CascadeType.ALL], orphanRemoval = true)
    var orderItems: MutableList<OrderItem> = mutableListOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    @JsonBackReference
    var product: Product

)