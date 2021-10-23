package com.snucse.pacemaker.service.users

import com.snucse.pacemaker.domain.User
import com.snucse.pacemaker.dto.UserDto
import com.snucse.pacemaker.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import com.snucse.pacemaker.exception.*

class UserServiceImpl(
        @Autowired private val userRepository: UserRepository,
        @Autowired private val bCryptPasswordEncoder: BCryptPasswordEncoder
): UserService {
    override fun getUserById(id: Long): User =
            userRepository.findById(id).orElseThrow { throw UserNotFoundException("can't find user by id: $id.")}


    override fun getUserByEmail(email: String): User =
            userRepository.findByEmail(email) ?: throw UserNotFoundException("can't find user by email: $email")


    override fun signUp(signUpReq: UserDto.SignUpReq): UserDto.SignUpRes {
        if(userRepository.existsByEmail(signUpReq.email))
            throw DuplicateEmailException("duplicate email: ${signUpReq.email}")

        if(userRepository.existsByUserInfoNickname(signUpReq.nickname))
            throw DuplicateNicknameException("duplicate nickname: ${signUpReq.nickname}")

        val createdUser = userRepository.save(signUpReq.toEntity(bCryptPasswordEncoder))

        val token = "TODO"

        return UserDto.SignUpRes.toDto(token, createdUser)

    }


    override fun signIn(signInReq: UserDto.SignInReq): UserDto.SignInRes {
        val findUser = getUserByEmail(signInReq.email)

        if(!findUser.isRightPassword(bCryptPasswordEncoder, signInReq.password))
            throw WrongPasswordException("wrong password exception")

        val token = "TODO"

        return UserDto.SignInRes.toDto(token, findUser)
    }

    override fun signOut() {
        TODO()
    }

    override fun updateNickname(updateNicknameRes: UserDto.updateNicknameRes, userId: Long): UserDto.UserRes {
        val user = getUserById(userId)

        try{
            user.updateNickname(updateNicknameRes.nickname)
        }
        catch (e: Exception){
            throw  UpdateNicknameException("update nickname exception msg: ${e.message}")
        }

        return user.toDto()
    }


}