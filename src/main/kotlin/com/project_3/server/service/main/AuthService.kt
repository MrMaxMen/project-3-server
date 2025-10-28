package com.project_3.server.service.main

import com.project_3.server.EmailAlreadyExistsException
import com.project_3.server.InvalidPasswordException
import com.project_3.server.UserNotFoundException
import com.project_3.server.models.Buyer
import com.project_3.server.repos.BuyerRepository
import com.project_3.server.security.JwtService
import org.springframework.stereotype.Service

@Service
class AuthService (private val buyerRepository: BuyerRepository,private val jwtService: JwtService) {

    fun register(name : String ,email : String , password : String ) : Buyer{
        if(buyerRepository.findByEmail(email) != null){
            throw EmailAlreadyExistsException()
        }

        val newBuyer = Buyer(
            name = name,
            email = email,
            password = password
        )

        return buyerRepository.save(newBuyer)
    }

    fun login(email: String , password: String): String{
        val buyer = buyerRepository.findByEmail(email)
            ?: throw UserNotFoundException()

        if(buyer.password != password){
            throw InvalidPasswordException()
        }

        val token = jwtService.generateToken(buyer.id!!)

        return token
    }
}