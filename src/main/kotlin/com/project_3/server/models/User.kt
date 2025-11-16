package com.project_3.server.models

import jakarta.persistence.*

@Entity
@Table(name = "users") // имя User зарезервированно в PostgreSQL
@Inheritance(strategy = InheritanceType.JOINED)
abstract class User (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null,

    var name: String,
    var email: String,
    var password: String,
)