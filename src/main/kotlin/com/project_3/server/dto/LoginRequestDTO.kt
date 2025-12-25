package com.project_3.server.dto

import com.project_3.server.security.Role

data class LoginRequestDTO(
    val email: String,
    val password: String,
    val role: Role,
)
