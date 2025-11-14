package com.project_3.server.models

import jakarta.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
class User (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null,

    var name: String,
    var email: String,
    var password: String,
)