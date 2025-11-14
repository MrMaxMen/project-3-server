package com.project_3.server

import com.project_3.server.models.Category
import com.project_3.server.models.Seller
import com.project_3.server.repos.CategoryRepository
import com.project_3.server.repos.SellerRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class DatabaseInitializer (
    private val categoryRepository: CategoryRepository,
    private val sellerRepository: SellerRepository
){
    @Bean
    fun initDatabase(): CommandLineRunner = CommandLineRunner {


        if(sellerRepository.count() == 0L){
            val testSeller = Seller(
                name = "moh",
                email = "moh@example.com",
                password = "moh123"
            )

            sellerRepository.save(testSeller)
            println("✅ test seller moh inserted into database.")
        } else {
            println("ℹ️ test seller moh already exist, skipping initialization.")
        }



        if (categoryRepository.count() == 0L) {
            val categories = listOf(
                Category(name = "Electronics"),
                Category(name = "Clothing"),
                Category(name = "Books"),
                Category(name = "Home"),
                Category(name = "Toys")
            )

            categoryRepository.saveAll(categories)
            println("✅ Default categories inserted into database.")
        } else {
            println("ℹ️ Categories already exist, skipping initialization.")
        }
    }

}