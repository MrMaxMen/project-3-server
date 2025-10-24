package com.project_3.server.models

import jakarta.persistence.*

@Entity
data class Test(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val name: String

)