package com.project_3.server

import com.project_3.server.models.Category
import com.project_3.server.repos.CategoryRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class DatabaseInitializer (
    private val categoryRepository: CategoryRepository
){
    @Bean
    fun initDatabase(): CommandLineRunner = CommandLineRunner {

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