package com.snucse.pacemaker.service.match

import com.snucse.pacemaker.domain.Match

import com.snucse.pacemaker.domain.MatchStatus
import com.snucse.pacemaker.domain.User
import com.snucse.pacemaker.domain.UserMatch
import com.snucse.pacemaker.dto.MatchDto
import com.snucse.pacemaker.dto.UserDto

import com.snucse.pacemaker.exception.MatchingProcessingYetException
import com.snucse.pacemaker.exception.UserMatchNotExistException
import com.snucse.pacemaker.exception.UserNotFoundException
import com.snucse.pacemaker.repository.MatchRepository
import com.snucse.pacemaker.repository.UserMatchRepository
import com.snucse.pacemaker.service.match.queue.RedisMatchQueue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.cache.CacheProperties

import javax.validation.constraints.Null

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class MatchServiceImpl(
        @Autowired private val userMatchRepository: UserMatchRepository,
): MatchService {

    override fun match(matchReq: MatchDto.MatchReq, userId: Long): MatchDto.MatchRes {

        val category = "${matchReq.distance}_${matchReq.participants}"

        // If it already exists in the match queue
        if(RedisMatchQueue.isExistUser(category, userId)){

            // If it exists in the match DB
            if(userMatchRepository.existsByUser_Id(userId)){
                val userMatches = userMatchRepository.findByUser_Id(userId)
                val users = mutableListOf<User>()
                if(userMatches != null
                        && userMatches.isNotEmpty()){
                    userMatches.forEach { userMatch ->
                        users.add(userMatch.user)
                    }
                }

                return MatchDto.MatchRes(
                        status = MatchStatus.DONE,
                        startDatetime = LocalDateTime.now(),
                        users = users.map { user -> user.toDto() }
                )
            }
        }
        else{
            RedisMatchQueue.add(category, userId)
        }

        return MatchDto.MatchRes(
                status = MatchStatus.PENDING,
                startDatetime = LocalDateTime.now(),
                users = mutableListOf()
        )
    }

    override fun getUserMatchByUserMatchId(userMatchId: Long): UserMatch =
            userMatchRepository.findById(userMatchId).orElseThrow { throw UserMatchNotExistException("can't find userMatch by id: $userMatchId.") }

    override fun inMatchPolling(inMatchReq: MatchDto.InMatchReq): MatchDto.InMatchRes {

        val userMatch = getUserMatchByUserMatchId(inMatchReq.userMatchId)

        // distance update
        userMatch.graph.add(inMatchReq.currentDistance)
        userMatch.currentDistance = inMatchReq.currentDistance

        // speed update
        if(userMatch.maximumSpeed < inMatchReq.currentSpeed){
            userMatch.maximumSpeed = inMatchReq.currentSpeed
        }
        userMatch.currentSpeed = inMatchReq.currentSpeed


        val inMatchRes = MatchDto.InMatchRes(
                matchUsers = mutableListOf(),
                alarmCategory = "NONE"
        )

        userMatch.id?.let { userMatchRepository.findAllByMatch_Id(it).forEach { otherUserMatch ->
            if(otherUserMatch.id != userMatch.id){
                inMatchRes.matchUsers.add(MatchDto.MatchUser(
                        email =  otherUserMatch.user.email,
                        nickname = otherUserMatch.user.nickname,
                        currentDistance = otherUserMatch.currentDistance,
                        currentSpeed = otherUserMatch.currentSpeed
                ))
            }
        } }

        if(userMatch.match.totalDistance - inMatchReq.currentDistance <= 100 && !userMatch.left100){
            userMatch.left100 = true
            inMatchRes.alarmCategory = "100M_LEFT"
        }

        if(userMatch.match.totalDistance - inMatchReq.currentDistance <= 50 && !userMatch.left50){
            userMatch.left50 = true
            inMatchRes.alarmCategory = "50M_LEFT"
        }

        if(userMatch.match.totalDistance - inMatchReq.currentDistance <= 0 && !userMatch.finish){
            userMatch.finish = true
            inMatchRes.alarmCategory = "FINISH"

            var firstOrder = true
            userMatch.id?.let { userMatchRepository.findAllByMatch_Id(it).forEach { otherUserMatch ->
                if(otherUserMatch.id != userMatch.id){
                    otherUserMatch.finishOther = true
                    if(otherUserMatch.finish){
                        firstOrder = false
                    }
                }
            } }

            if(firstOrder){
                inMatchRes.alarmCategory = "FIRST_PLACE"
            }

        }

        if(userMatch.finishOther){
            userMatch.finishOther = false
            inMatchRes.alarmCategory = "FINISH_OTHER"
        }


        // Overtaken, Overtaking
        if(userMatch.graph.size >= 10){
            userMatch.id?.let { userMatchRepository.findAllByMatch_Id(it).forEach { otherUserMatch ->
                if(otherUserMatch.id != userMatch.id && otherUserMatch.graph.size >= 10){
                    val userGraphSize = userMatch.graph.size
                    val otherUserGraphSize = otherUserMatch.graph.size

                    if(userMatch.graph[userGraphSize-2] < otherUserMatch.graph[otherUserGraphSize-2]){
                        if(userMatch.graph[userGraphSize-1] > otherUserMatch.graph[otherUserGraphSize-1]){
                            inMatchRes.alarmCategory = "OVERTAKING"
                        }
                    }
                    else if(userMatch.graph[userGraphSize-2] > otherUserMatch.graph[otherUserGraphSize-2]){
                        if(userMatch.graph[userGraphSize-1] < otherUserMatch.graph[otherUserGraphSize-1]){
                            inMatchRes.alarmCategory = "OVERTAKEN"
                        }
                    }
                }
            } }
        }



        var done = true
        userMatch.id?.let { userMatchRepository.findAllByMatch_Id(it).forEach { otherUserMatch ->
            if(!otherUserMatch.finish){
                done = false
            }
        } }

        if(done){
            inMatchRes.alarmCategory = "DONE"
        }



        return inMatchRes
    }
}