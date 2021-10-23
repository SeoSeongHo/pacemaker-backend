package com.snucse.pacemaker.dto

import com.snucse.pacemaker.domain.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class UserDto {
    data class SignInReq(
            var email: String,
            var password: String
    )

    data class SignInRes(
            val token: String,
            val user: UserRes
    ){
        companion object{
            fun toDto(token: String, user: User): SignInRes{
                return SignInRes(
                        token = token,
                        user = user.toDto()
                )
            }
        }
    }

    data class SignUpReq(
            var email: String,
            var password: String,
            var nickname: String
    ){
        fun toEntity(bCryptPasswordEncoder: BCryptPasswordEncoder) = User(
                email = email,
                password = bCryptPasswordEncoder.encode(password),
                nickname = nickname,
                rating = "Silver"
        )
    }

    data class SignUpRes(
            var token: String,
            var user: UserRes
    ){
        companion object{
            fun toDto(token: String, user:User): SignUpRes {
                return SignUpRes(
                        token = token,
                        user = user.toDto()
                )
            }
        }
    }

    data class UserRes(
            var id: Long,
            var email: String,
            var nickname: String,
            var rating: String
    )

    data class updateNicknameRes(
            var nickname: String
    )



}