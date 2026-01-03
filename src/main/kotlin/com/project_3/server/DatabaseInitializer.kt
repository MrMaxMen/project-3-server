package com.project_3.server

import com.project_3.server.models.Category
import com.project_3.server.models.Product
import com.project_3.server.models.ProductGroup
import com.project_3.server.models.logistics.PickupPoint
import com.project_3.server.models.stock.Stock
import com.project_3.server.models.users.Buyer
import com.project_3.server.models.users.Seller
import com.project_3.server.repos.BuyerRepository
import com.project_3.server.repos.CategoryRepository
import com.project_3.server.repos.PickupPointRepository
import com.project_3.server.repos.ProductGroupRepository
import com.project_3.server.repos.ProductOnStockRepository
import com.project_3.server.repos.ProductRepository
import com.project_3.server.repos.SellerRepository
import com.project_3.server.repos.StockRepository
import com.project_3.server.service.OrthodromeService
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class DatabaseInitializer(
        private val categoryRepository: CategoryRepository,
        private val sellerRepository: SellerRepository,
        private val buyerRepository: BuyerRepository,
        private val productGroupRepository: ProductGroupRepository,
        private val productRepository: ProductRepository,
        private val stockRepository: StockRepository,
        private val productOnStockRepository: ProductOnStockRepository,
        private val pickupPointRepository: PickupPointRepository,
        private val passwordEncoder: PasswordEncoder,
        private val orthodromeService: OrthodromeService
) {
        @Bean
        fun initDatabase(): CommandLineRunner = CommandLineRunner {
                val categories = List(5) { Category(name = "Category $it") }
                categoryRepository.saveAll(categories)

                val buyers =
                        List(5) {
                                Buyer(
                                        name = "Buyer $it",
                                        email = "BuyerMail$it@gmail.com",
                                        password = passwordEncoder.encode("password$it")
                                )
                        }
                buyerRepository.saveAll(buyers)

                val sellers =
                        List(5) {
                                Seller(
                                        name = "Seller $it",
                                        email = "SellerMail$it@gmail.com",
                                        password = passwordEncoder.encode("password$it")
                                )
                        }
                sellerRepository.saveAll(sellers)

                val productGroups =
                        List(10) {
                                ProductGroup(
                                        name = "Brand $it",
                                        category = categories[it % categories.size],
                                        seller = sellers[it % sellers.size]
                                )
                        }
                productGroupRepository.saveAll(productGroups)

                val products =
                        List(20) {
                                Product(
                                        name = "Product $it",
                                        description = "Description for Product $it",
                                        weightGrams = 500 + (it * 50),
                                        lengthMm = 100 + (it * 10),
                                        widthMm = 80 + (it * 5),
                                        heightMm = 50 + (it * 5),
                                        mediaURLs =
                                                mutableListOf("https://example.com/media$it.jpg"),
                                        basePrice = 10.0 + it,
                                        currentPrice = 10.0 + it,
                                        seller = sellers[it % sellers.size],
                                        category = categories[it % categories.size],
                                        productGroup = productGroups[it % productGroups.size]
                                )
                        }
                productRepository.saveAll(products)

                val stocks =
                        List(3) {
                                Stock(
                                        name = "Stock $it",
                                        address = "Address $it",
                                        latitude = 10.0 + it,
                                        longitude = 20.0 + it
                                )
                        }
                stockRepository.saveAll(stocks)

                //                val productOnStocks = List (40) { ProductOnStock(
                //                        stock = stockRepository.findAll().random(),
                //                        product = products[it % products.size],
                //                        quantity = 100 + (it % products.size)
                //                ) }
                //                productOnStockRepository.saveAll(productOnStocks)

                val pickupPoints =
                        List(5) {
                                PickupPoint(
                                        name = "Pickup Point $it",
                                        address = "Pickup Address $it",
                                        latitude = 30.0 + it,
                                        longitude = 40.0 + it
                                )
                        }
                pickupPointRepository.saveAll(pickupPoints)

                println("✅Database initialized with sample data.")

                orthodromeService.calculateAllOrthodromes()
        }
}
