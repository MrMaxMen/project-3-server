package com.project_3.server.dto

import com.project_3.server.security.Role


data class RegisterRequestDTO(
    val name: String,
    val email: String,
    val password: String,
    val role: Role,
)