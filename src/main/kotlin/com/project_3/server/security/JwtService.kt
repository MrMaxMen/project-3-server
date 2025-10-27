package com.project_3.server.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService {

    // в этом токене шифруется почта пользователя

    private val secretKey : SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)

    private val expirationTime = 24 * 60 * 60 * 1000L

    fun generateToken(email : String) : String {

        val now = Date()
        val expiryDate = Date(now.time + expirationTime)

        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(secretKey)
            .compact()
    }

    fun validateToken(token : String ) : Boolean{
        return try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
            true
        }catch (ex: Exception){
            false
        }
    }

    fun extractEmail(token: String): String? {
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .body
            claims.subject
        } catch (ex: Exception) {
            null
        }
    }


}
