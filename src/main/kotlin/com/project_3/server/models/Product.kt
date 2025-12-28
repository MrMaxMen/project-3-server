package com.project_3.server.models

import com.fasterxml.jackson.annotation.JsonBackReference
import com.project_3.server.models.stock.ProductOnStock
import com.project_3.server.models.users.Seller
import jakarta.persistence.*

@Entity
class Product(

        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        var name: String,

        var description: String,

        var mediaURLs: MutableList<String> = mutableListOf(),

        var basePrice: Double, // may be changed by the seller

        var discount: Double? = null,

        var currentPrice: Double , //base price - discount

        var rating: Double? = null, // calculatable

        var reviewCount: Int? = null,

        //        var totalQuantity: Int, // под вопросом (это значение лучше вычислять)

        var priceHistory: MutableList<Double> = mutableListOf(),

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn
        var seller: Seller,

        @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        var reviewList: MutableSet<Review> = mutableSetOf(),

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn
        var category: Category,

        @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], orphanRemoval = true)
        var productInOrders: MutableSet<ProductInOrder> = mutableSetOf(),

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn
        @JsonBackReference
        var productGroup: ProductGroup,

        @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], orphanRemoval = true)
        var productOnStocks: MutableSet<ProductOnStock> = mutableSetOf()
)
