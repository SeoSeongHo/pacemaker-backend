package com.snucse.pacemaker.config.jwt

import com.snucse.pacemaker.dto.AuthPrincipal
import com.snucse.pacemaker.exception.JwtValidationException
import com.snucse.pacemaker.repository.BlackListRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Component
import java.util.*
import javax.servlet.http.HttpServletRequest

@Component
class JwtTokenProvider(
        @Value("\${jwt.access.secret_key}") private val accessTokenSecretKey: String,
        @Value("\${jwt.access.expire_time}") private val accessTokenExpiredTime: Long,
        @Autowired private val blackListRepository: BlackListRepository
) {

    fun createToken(userId: Long): String{
        val claims: Claims = Jwts.claims().setSubject(userId.toString())
        val expiredTime = Date(Date().time + accessTokenExpiredTime)
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date())
                .setExpiration(expiredTime)
                .signWith(SignatureAlgorithm.HS512, accessTokenSecretKey)
                .compact()
    }

    fun getAuthentication(token: String): Authentication {
        val claims = getClaims(token)
        return UsernamePasswordAuthenticationToken(AuthPrincipal.of(claims), "", emptyList<GrantedAuthority>())
    }

    fun resolveToken(req: HttpServletRequest): String?{
        val token: String? = req.getHeader("Authorization")
        if(token == null || !token.startsWith("Bearer ")) return null

        var token22 = token.substring(7, token.length)
        return token.substring(7, token.length)
    }

    fun validateToken(token: String): Boolean{
        if(blackListRepository.existsByToken(token)) throw JwtValidationException("jwt is in black list. please reissued.")
        try{
            val validation = getClaims(token).expiration.before(Date())
            if(getClaims(token).expiration.before(Date())) return false
            return true
        }
        catch (e: Exception){
            throw JwtValidationException("failed to validate jwt.")
        }
    }

    fun getClaims(token: String): Claims{
        var claims = Jwts.parser().setSigningKey(accessTokenSecretKey).parseClaimsJws(token).body
        return Jwts.parser().setSigningKey(accessTokenSecretKey).parseClaimsJws(token).body
    }
}