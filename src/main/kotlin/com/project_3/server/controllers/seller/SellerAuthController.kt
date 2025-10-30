package com.project_3.server.controllers.seller

import com.project_3.server.models.Seller
import com.project_3.server.service.seller.SellerAuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/seller/auth")
class SellerAuthController(private val sellerAuthService: SellerAuthService) {

    data class TokenResponse(val token: String)

    data class RegisterRequest(val name: String, val email: String, val password: String)
    data class LoginRequest(val email: String, val password: String)

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest) : ResponseEntity<Seller>{
        val seller = sellerAuthService.register(request.name, request.email, request.password)
        return ResponseEntity.ok(seller)
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<TokenResponse> {
        val token = TokenResponse(sellerAuthService.login(request.email, request.password))
        return ResponseEntity.ok(token)
    }

}