package com.snucse.pacemaker.service.users

import com.snucse.pacemaker.domain.User
import com.snucse.pacemaker.dto.UserDto
import com.snucse.pacemaker.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class UserServiceImpl(
        @Autowired private val userRepository: UserRepository,
        @Autowired private val bCryptPasswordEncoder: BCryptPasswordEncoder
): UserService {
    override fun getUserById(id: Long): User =
            userRepository.findById(id).orElseThrow(throw TODO())


    override fun getUserByEmail(email: String): User =
            userRepository.findByEmail(email) ?: throw TODO()


    override fun signUp(signUpReq: UserDto.SignUpReq): UserDto.SignUpRes {
        if(userRepository.existsByEmail(signUpReq.email))
            throw TODO()

        if(userRepository.existsByUserInfoNickname(signUpReq.nickname))
            throw TODO()

        val createdUser = userRepository.save(signUpReq.toEntity(bCryptPasswordEncoder))

        val token = "TODO"

        return UserDto.SignUpRes.toDto(token, createdUser)

    }


    override fun signIn(signInReq: UserDto.SignInReq): UserDto.SignInReq {
        TODO("Not yet implemented")
    }


    override fun isDuplicateEmail(email: String): Boolean {
        TODO("Not yet implemented")
    }

}