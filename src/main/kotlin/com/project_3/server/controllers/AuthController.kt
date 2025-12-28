package com.project_3.server.controllers

import com.project_3.server.dto.LoginRequestDTO
import com.project_3.server.dto.RegisterRequestDTO
import com.project_3.server.models.users.Buyer
import com.project_3.server.models.users.User
import com.project_3.server.security.Role
import com.project_3.server.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.security.core.Authentication
import kotlin.toString

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {




    @PostMapping("/register")
    fun register(
        @RequestBody registerRequest: RegisterRequestDTO,
    ) : ResponseEntity<User> {

        val user = authService.register(registerRequest)
        return ResponseEntity.ok(user)
    }

    @PostMapping("/login")
    fun login(
        @RequestBody loginRequest: LoginRequestDTO
    ): ResponseEntity<TokenResponse> {
        val token = TokenResponse(authService.login(loginRequest))
        return ResponseEntity.ok(token)
    }

    data class TokenResponse(val token: String)
}