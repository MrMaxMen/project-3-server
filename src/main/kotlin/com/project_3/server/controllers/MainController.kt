package com.project_3.server.controllers

import com.project_3.server.models.Test
import com.project_3.server.service.TestService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class MainController(private val service: TestService){

    @GetMapping("/test")
    fun test(): List<Test> {

        return service.getAll()
    }



    @PostMapping("/add")
    fun add(){
        service.add(Test(null, "My Name"))
    }

}