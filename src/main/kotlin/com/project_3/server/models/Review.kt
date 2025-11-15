package com.project_3.server.models

import jakarta.persistence.*
import java.time.LocalDate

@Entity
class Review(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,


    var rating: Int,
    var comment: String,
    var date : LocalDate,


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn()
    var item: Item,


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn()
    var buyer: Buyer,

)