package com.project_3.server

import com.project_3.server.models.Buyer
import com.project_3.server.models.Category
import com.project_3.server.models.Item
import com.project_3.server.models.PickupPoint
import com.project_3.server.models.Product
import com.project_3.server.models.Seller
import com.project_3.server.models.Stock
import com.project_3.server.models.StockItem
import com.project_3.server.repos.BuyerRepository
import com.project_3.server.repos.CategoryRepository
import com.project_3.server.repos.ItemRepository
import com.project_3.server.repos.PickupPointRepository
import com.project_3.server.repos.ProductRepository
import com.project_3.server.repos.SellerRepository
import com.project_3.server.repos.StockItemRepository
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
        private val productRepository: ProductRepository,
        private val itemRepository: ItemRepository,
        private val stockRepository: StockRepository,
        private val stockItemRepository: StockItemRepository,
        private val pickupPointRepository: PickupPointRepository,
        private val passwordEncoder: PasswordEncoder,
        private val orthodromeService: OrthodromeService
) {
        @Bean
        fun initDatabase(): CommandLineRunner = CommandLineRunner {

                val categories = List (5) { Category(
                        name = "Category $it"
                ) }
                categoryRepository.saveAll(categories)



                val buyers = List (5) { Buyer(
                    name = "Buyer $it",
                    email = "BuyerMail$it@gmail.com",
                    password = passwordEncoder.encode("password$it")
                )}
                buyerRepository.saveAll(buyers)



                val sellers = List (5) { Seller(
                        name = "Seller $it",
                        email = "SellerMail$it@gmail.com",
                        password = passwordEncoder.encode("password$it")
                )}
                sellerRepository.saveAll(sellers)



                val products = List (10) { Product(
                        brand = "Brand $it",
                        category = categories[it % categories.size],
                        seller = sellers[it % sellers.size]
                ) }
                productRepository.saveAll(products)



                val items = List (20) { Item(
                        name = "Item $it",
                        description = "Description for Item $it",
                        mediaURLs = mutableListOf("https://example.com/media$it.jpg"),
                        basePrice = 10.0 + it,
                        currentPrice = 10.0 + it,
                        seller = sellers[it % sellers.size],
                        category = categories[it % categories.size],
                        product = products[it % products.size]
                ) }
                itemRepository.saveAll(items)



                val stocks = List (3) { Stock(
                        name = "Stock $it",
                        address = "Address $it",
                        latitude = 10.0 + it,
                        longitude = 20.0 + it
                ) }
                stockRepository.saveAll(stocks)



                val stockItems = List (40) { StockItem(
                        stock = stockRepository.findAll().random(),
                        item = items[it % items.size],
                        quantity = 100 + (it % items.size)
                ) }
                stockItemRepository.saveAll(stockItems)



                val pickupPoints =  List(5) { PickupPoint(
                        name = "Pickup Point $it",
                        address = "Pickup Address $it",
                        latitude = 30.0 + it,
                        longitude = 40.0 + it
                ) }
                pickupPointRepository.saveAll(pickupPoints)



                println("✅Database initialized with sample data.")


                orthodromeService.calculateAllOrthodromes()



        }
}
