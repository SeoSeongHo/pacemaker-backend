package com.snucse.pacemaker.dto

class UserDto {
    data class SignInReq(
            var email: String,
            var password: String
    )

    data class SignInRes(
            var email: String,
            var password: String
    )

//    data class SignUpReq(
//
//    )
//
//    data class SignUpRes(
//
//    )
}