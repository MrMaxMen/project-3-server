package com.project_3.server.service

import com.project_3.server.models.Test
import com.project_3.server.repos.TestRepository
import org.springframework.stereotype.Service


@Service
class TestService(private val testRepository: TestRepository) {

    fun getAll() = testRepository.findAll()

    fun add(test : Test) {
        testRepository.save(test)
    }

}