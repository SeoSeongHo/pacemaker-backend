package com.snucse.pacemaker.service.users

import com.snucse.pacemaker.domain.User
import com.snucse.pacemaker.dto.*

interface UserService {
    fun getUserById(id: Long): User
    fun getUserByEmail(email: String): User

    fun signUp(signUpReq: UserDto.SignUpReq): UserDto.SignUpRes
    fun signIn(signInReq: UserDto.SignInReq): UserDto.SignInReq
//    fun logout()
//
//    fun updateUserInfo()

    fun isDuplicateEmail(email: String): Boolean
}