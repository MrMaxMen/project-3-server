package com.project_3.server.controllers

import com.project_3.server.repos.CategoryRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/public")
class PublicController (
    private val categoryRepository: CategoryRepository
) {

    @GetMapping("/categories")
    fun getAllCategories(): ResponseEntity<List<String>> {
        return ResponseEntity.ok(categoryRepository.findAll().map { it.name })
    }



}