package com.snucse.pacemaker.dto

import com.snucse.pacemaker.domain.User
import com.snucse.pacemaker.domain.UserInfo
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class UserDto {
    data class SignInReq(
            var email: String,
            var password: String
    )

    data class SignInRes(
            var email: String,
            var password: String
    )

    data class SignUpReq(
            var email: String,
            var password: String,
            var nickname: String
    ){
        fun toEntity(bCryptPasswordEncoder: BCryptPasswordEncoder) = User(
                email = email,
                password = bCryptPasswordEncoder.encode(password),
                userInfo = UserInfo(
                        nickname = nickname
                )
        )
    }

    data class SignUpRes(
            var token: String,
            var id: Long,
            var email: String
    ){
        companion object{
            fun toDto(token: String, user:User): SignUpRes {
                return SignUpRes(
                        token = token,
                        id = user.id!!,
                        email = user.email
                )
            }
        }
    }
}