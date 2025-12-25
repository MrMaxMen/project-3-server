package com.project_3.server.controllers

import com.project_3.server.service.UserService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/user")
class UserController (
    private val userService : UserService
){


    // get user profile info(both)


    //get cart(buyer)

    //get favorites(buyer)

    //get my products(seller)




}