package com.project_3.server.models

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.*

@Entity
class Item(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
        var name: String,
        var description: String,
        var mediaURLs: MutableList<String> = mutableListOf(),
        var basePrice: Double,
        var discount: Double? = null,
        var currentPrice: Double,
        var rating: Double? = null,
        var reviewCount: Int? = null,

        //        var totalQuantity: Int, // под вопросом (это значение лучше вычислять)

        var priceHistory: MutableList<Double> = mutableListOf(),
        @ManyToOne(fetch = FetchType.LAZY) @JoinColumn var seller: Seller,
        @OneToMany(mappedBy = "item", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        var reviewList: MutableSet<Review> = mutableSetOf(),
        @ManyToOne(fetch = FetchType.LAZY) @JoinColumn var category: Category,
        @OneToMany(mappedBy = "item", cascade = [CascadeType.ALL], orphanRemoval = true)
        var orderItems: MutableSet<OrderItem> = mutableSetOf(),
        @ManyToOne(fetch = FetchType.LAZY) @JoinColumn @JsonBackReference var product: Product?,
        @OneToMany(mappedBy = "item", cascade = [CascadeType.ALL], orphanRemoval = true)
        var stockItems: MutableSet<StockItem> = mutableSetOf()
)
