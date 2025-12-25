package com.project_3.server.service

import com.project_3.server.dto.LoginRequestDTO
import com.project_3.server.dto.RegisterRequestDTO
import com.project_3.server.exceptions.EmailAlreadyExistsException
import com.project_3.server.exceptions.InvalidPasswordException
import com.project_3.server.exceptions.UserNotFoundByEmailException
import com.project_3.server.models.Buyer
import com.project_3.server.models.Seller
import com.project_3.server.models.User
import com.project_3.server.repos.BuyerRepository
import com.project_3.server.repos.SellerRepository
import com.project_3.server.security.JwtService
import com.project_3.server.security.Role
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
        private val buyerRepository: BuyerRepository,
        private val sellerRepository: SellerRepository,
        private val jwtService: JwtService,
        private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun register(registerRequest: RegisterRequestDTO): User {

        when (registerRequest.role) {
            Role.BUYER -> {
                if (buyerRepository.findByEmail(registerRequest.email) != null) {
                    throw EmailAlreadyExistsException(registerRequest.email)
                }

                val newBuyer =
                        Buyer(
                                name = registerRequest.name,
                                email = registerRequest.email,
                                password = passwordEncoder.encode(registerRequest.password),
                                favorites = mutableSetOf()
                        )

                return buyerRepository.save(newBuyer)
            }
            Role.SELLER -> {
                if (sellerRepository.findByEmail(registerRequest.email) != null) {
                    throw EmailAlreadyExistsException(registerRequest.email)
                }

                val newSeller =
                        Seller(
                                name = registerRequest.name,
                                email = registerRequest.email,
                                password = passwordEncoder.encode(registerRequest.password)
                        )

                return sellerRepository.save(newSeller)
            }
        }
    }

    fun login(loginRequest: LoginRequestDTO): String {

        when (loginRequest.role) {
            Role.BUYER -> {
                val buyer =
                        buyerRepository.findByEmail(loginRequest.email)
                                ?: throw UserNotFoundByEmailException(loginRequest.email)

                if (!passwordEncoder.matches(loginRequest.password, buyer.password)) {
                    throw InvalidPasswordException()
                }

                val token = jwtService.generateToken(buyer.id!!, Role.BUYER)

                return token
            }
            Role.SELLER -> {
                val seller =
                        sellerRepository.findByEmail(loginRequest.email)
                                ?: throw UserNotFoundByEmailException(loginRequest.email)

                if (!passwordEncoder.matches(loginRequest.password, seller.password)) {
                    throw InvalidPasswordException()
                }

                val token = jwtService.generateToken(seller.id!!, Role.SELLER)

                return token
            }
        }
    }
}
