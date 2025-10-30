package com.project_3.server.service.seller

import com.project_3.server.EmailAlreadyExistsException
import com.project_3.server.InvalidPasswordException
import com.project_3.server.UserNotFoundException
import com.project_3.server.models.Seller
import com.project_3.server.repos.SellerRepository
import com.project_3.server.security.JwtService
import org.springframework.stereotype.Service

@Service
class SellerAuthService (private val sellerRepository: SellerRepository, private val jwtService: JwtService) {

    fun register(name : String ,email : String , password : String ) : Seller {
        if(sellerRepository.findByEmail(email) != null){
            throw EmailAlreadyExistsException()
        }

        val newSeller = Seller(
            name = name,
            email = email,
            password = password
        )

        return sellerRepository.save(newSeller)
    }

    fun login(email: String , password: String): String{
        val seller = sellerRepository.findByEmail(email)
            ?: throw UserNotFoundException()

        if(seller.password != password){
            throw InvalidPasswordException()
        }

        val token = jwtService.generateToken(seller.id!!)

        return token
    }
}