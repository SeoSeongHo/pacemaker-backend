package com.snucse.pacemaker.config.jwt

import com.snucse.pacemaker.dto.AuthPrincipal
import com.snucse.pacemaker.exception.JwtValidationException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.*
import javax.servlet.http.HttpServletRequest

@Component
class JwtTokenProvider(
        @Value("\${jwt.secret_key}") private val jwtSecretKey: String,
        @Value("\${jwt.secret_key}") private val jwtTTL: Long
) {

    fun createToken(userId: Long): String{
        val claims: Claims = Jwts.claims().setSubject(userId.toString())
        val expiredTime = Date(Date().time + jwtTTL)
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date())
                .setExpiration(expiredTime)
                .signWith(SignatureAlgorithm.HS512, jwtSecretKey)
                .compact()
    }

    fun getAuthentication(token: String): Authentication {
        val claims = getClaims(token)
        return UsernamePasswordAuthenticationToken(AuthPrincipal.of(claims), "")
    }

    fun resolveToken(req: HttpServletRequest): String?{
        val token: String? = req.getHeader("Authorization")
        if(token == null || !token.startsWith("Bearer ")) return null

        return token.substring(7, token.length)
    }

    fun validateToken(token: String): Boolean{
        try{
            if(getClaims(token).expiration.before(Date())) return false
            return true
        }
        catch (e: Exception){
            throw JwtValidationException("failed to validate jwt.")
        }
    }

    fun getClaims(token: String): Claims{
        return Jwts.parser().setSigningKey(jwtSecretKey).parseClaimsJws(token).body
    }
}