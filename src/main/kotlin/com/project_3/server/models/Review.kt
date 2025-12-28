package com.project_3.server.models

import com.project_3.server.models.users.Buyer
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
    var product: Product,


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn()
    var buyer: Buyer,

    )