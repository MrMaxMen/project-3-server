package com.project_3.server.controllers.main

import com.project_3.server.models.Buyer
import com.project_3.server.security.JwtService
import com.project_3.server.service.main.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/main/auth")
class AuthController(private val authService: AuthService) {

    data class TokenResponse(val token: String)

    data class RegisterRequest(val name: String, val email: String, val password: String)
    data class LoginRequest(val email: String, val password: String)

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest) : ResponseEntity<Buyer>{
        val buyer = authService.register(request.name, request.email, request.password)
        return ResponseEntity.ok(buyer)
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<TokenResponse> {
        val token = TokenResponse(authService.login(request.email, request.password))
        return ResponseEntity.ok(token)
    }

}