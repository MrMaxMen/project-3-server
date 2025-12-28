package com.project_3.server.models.users

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.Table

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