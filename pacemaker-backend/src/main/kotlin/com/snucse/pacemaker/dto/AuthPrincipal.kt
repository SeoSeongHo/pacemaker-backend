package com.snucse.pacemaker.dto

import io.jsonwebtoken.Claims

data class AuthPrincipal(
        var userId: Long
) {

    companion object{
        fun of(claims: Claims) = AuthPrincipal(
                userId = claims.subject.toLong()
        )
    }
}