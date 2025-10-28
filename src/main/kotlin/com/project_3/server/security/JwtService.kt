package com.project_3.server.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService {

    // в этом токене шифруется id пользователя

    private val secretKey : SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)

    private val expirationTime = 24 * 60 * 60 * 1000L

    fun generateToken(id : Long) : String {

        val now = Date()
        val expiryDate = Date(now.time + expirationTime)

        return Jwts.builder()
            .setSubject(id.toString())
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


}
