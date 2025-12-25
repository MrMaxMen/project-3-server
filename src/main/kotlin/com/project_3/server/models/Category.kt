package com.project_3.server.models

import jakarta.persistence.*

@Entity
class Category(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
        var name: String,
        @OneToMany(mappedBy = "category", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        var itemsList: MutableSet<Item> = mutableSetOf(),
        @OneToMany(mappedBy = "category", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        var productsList: MutableSet<Item> = mutableSetOf()
)
