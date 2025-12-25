package com.project_3.server.service

import com.project_3.server.repos.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService (
    private val userRepository: UserRepository
){



}