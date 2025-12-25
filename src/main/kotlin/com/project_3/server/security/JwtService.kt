package com.project_3.server.security

import io.jsonwebtoken.*
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import java.util.Date
import javax.crypto.SecretKey
import org.springframework.stereotype.Service

@Service
class JwtService {

    // В токене кодируется: ID пользователя + его роль (BUYER или SELLER)
    
    private val secretKey: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
    private val expirationTime = 24 * 60 * 60 * 1000L // 24 часа



    fun generateToken(id: Long, role: Role): String {
        val now = Date()
        val expiryDate = Date(now.time + expirationTime)

        return Jwts.builder()
                .setSubject(id.toString()) // ID пользователя
                .claim("role", role.name) // Роль: "BUYER" или "SELLER"
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey)
                .compact()
    }


    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
            true
        } catch (ex: Exception) {
            false
        }
    }


    fun extractId(token: String): Long? {
        return try {
            val claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .body
            claims.subject.toLong()
        } catch (ex: Exception) {
            null
        }
    }


    fun extractRole(token: String): Role? {
        return try {
            val claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .body
            val roleString = claims["role"] as String
            Role.valueOf(roleString) // Преобразуем строку в enum
        } catch (ex: Exception) {
            null
        }
    }


    fun extractToken(authHeader: String?): String? {
        if (authHeader == null) return null
        return if (authHeader.startsWith("Bearer ")) {
            authHeader.substring(7) // Убираем "Bearer "
        } else {
            authHeader
        }
    }
}