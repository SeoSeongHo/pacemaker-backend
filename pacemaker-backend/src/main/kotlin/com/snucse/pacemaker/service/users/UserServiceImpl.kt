package com.snucse.pacemaker.service.users

import com.snucse.pacemaker.config.jwt.JwtTokenProvider
import com.snucse.pacemaker.domain.BlackList
import com.snucse.pacemaker.domain.MatchStatus
import com.snucse.pacemaker.domain.User
import com.snucse.pacemaker.dto.OAuthDto
import com.snucse.pacemaker.dto.UserDto
import com.snucse.pacemaker.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import com.snucse.pacemaker.exception.*
import com.snucse.pacemaker.repository.BlackListRepository
import com.snucse.pacemaker.repository.UserMatchRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserServiceImpl(
        @Autowired private val userRepository: UserRepository,
        @Autowired private val bCryptPasswordEncoder: BCryptPasswordEncoder,
        @Autowired private val jwtTokenProvider: JwtTokenProvider,
        @Autowired private val blackListRepository: BlackListRepository,
        @Autowired private val userMatchRepository: UserMatchRepository
): UserService {
    override fun getUserById(id: Long): User =
            userRepository.findById(id).orElseThrow { throw UserNotFoundException("can't find user by id: $id.")}


    override fun getUserByEmail(email: String): User =
            userRepository.findByEmail(email) ?: throw UserNotFoundException("can't find user by email: $email")


    override fun signUp(signUpReq: UserDto.SignUpReq): UserDto.SignUpRes {
        if(userRepository.existsByEmail(signUpReq.email))
            throw DuplicateEmailException("duplicate email: ${signUpReq.email}")

        if(userRepository.existsByNickname(signUpReq.nickname))
            throw DuplicateNicknameException("duplicate nickname: ${signUpReq.nickname}")

        val createdUser = userRepository.save(signUpReq.toEntity(bCryptPasswordEncoder))

        val token = jwtTokenProvider.createToken(createdUser.id!!)

        return UserDto.SignUpRes.toDto(token, createdUser)

    }


    override fun signIn(signInReq: UserDto.SignInReq): UserDto.SignInRes {
        val findUser = getUserByEmail(signInReq.email)

        if(!findUser.isRightPassword(bCryptPasswordEncoder, signInReq.password))
            throw WrongPasswordException("wrong password exception")

        val token = jwtTokenProvider.createToken(findUser.id!!)

        return UserDto.SignInRes.toDto(token, findUser)
    }

    override fun signOut(oAuthDto: OAuthDto.OAuthReq) {
        blackListRepository.save(BlackList(token = oAuthDto.token))
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

    override fun getUserHistory(userId: Long): UserDto.UserHistoryRes {
        val userMatches = userMatchRepository.findByUser_Id(userId)!!.filter { userMatch -> userMatch.match.matchStatus == MatchStatus.DONE}

        val userHistoryRes = UserDto.UserHistoryRes()

        if(userMatches.count() < 1){
            return userHistoryRes
        }

        val reversedUserMatches = userMatches.reversed()

        reversedUserMatches.forEach {
            userHistoryRes.userHistoryList.add(
                    it.toUserHistoryDto()
            )
        }

        return userHistoryRes
    }


}