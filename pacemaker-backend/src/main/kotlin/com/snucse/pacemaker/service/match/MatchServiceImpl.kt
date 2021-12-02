package com.snucse.pacemaker.service.match

import com.snucse.pacemaker.domain.Match

import com.snucse.pacemaker.domain.MatchStatus
import com.snucse.pacemaker.domain.User
import com.snucse.pacemaker.dto.MatchDto
import com.snucse.pacemaker.dto.UserDto

import com.snucse.pacemaker.exception.MatchingProcessingYetException
import com.snucse.pacemaker.exception.UserMatchNotExistException
import com.snucse.pacemaker.repository.MatchRepository
import com.snucse.pacemaker.repository.UserMatchRepository
import com.snucse.pacemaker.service.match.queue.RedisMatchQueue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.cache.CacheProperties

import javax.validation.constraints.Null

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
@Transactional
class MatchServiceImpl(
        @Autowired private val userMatchRepository: UserMatchRepository,
        @Autowired private val matchRepository: MatchRepository
): MatchService {

    override fun match(matchReq: MatchDto.MatchReq, userId: Long): MatchDto.MatchRes {

        val category = "${matchReq.distance}_${matchReq.participants}"

        var now = LocalDateTime.now().plusSeconds(5)
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        var text = formatter.format(now)

        // If it already exists in the match queue
        if(RedisMatchQueue.isExistUser(category, userId)){

            // If it exists in the match DB
            if(userMatchRepository.existsByUser_Id(userId)){
                // TODO 중복제거
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
                        startDatetime = LocalDateTime.parse(text, formatter),
                        users = users.map { user -> user.toDto() }
                )
            }
        }
        else{
            RedisMatchQueue.add(category, userId)
        }

        return MatchDto.MatchRes(
                status = MatchStatus.PENDING,
                startDatetime = LocalDateTime.parse(text, formatter),
                users = mutableListOf()
        )
    }
/*
    override fun inMatchPolling(matchId: Long, inMatchReq: MatchDto.InMatchReq): MatchDto.InMatchRes {

        val match = matchRepository.findById(matchId).orElseThrow()

        // usermatch

        // foreach

        // distance update
        userMatch!!.userHistory.graph.add(inMatchReq.distance)
        userMatch.distance = inMatchReq.distance

        // velocity update
        if(userMatch.userHistory.maximumVelocity < inMatchReq.speed){
            userMatch.userHistory.maximumVelocity = inMatchReq.speed
        }
        userMatch.speed = inMatchReq.speed


        val inMatchRes = MatchDto.InMatchRes(
                matchUsers = mutableListOf(),
                alarmCategory = "NONE"
        )

        userMatch.id?.let { userMatchRepository.findAllByMatch_Id(it).forEach { otherUserMatch ->
            if(otherUserMatch.id != userMatch.id){
                inMatchRes.matchUsers.add(MatchDto.MatchUser(
                        email =  otherUserMatch.user.email,
                        nickname = otherUserMatch.user.nickname,
                        distance = otherUserMatch.distance,
                        speed = otherUserMatch.speed
                ))
            }
        } }

        if(userMatch.match.distance - inMatchReq.distance <= 100 && !userMatch.left100){
            userMatch.left100 = true
            inMatchRes.alarmCategory = "100M_LEFT"
        }

        if(userMatch.match.distance - inMatchReq.distance <= 50 && !userMatch.left50){
            userMatch.left50 = true
            inMatchRes.alarmCategory = "50M_LEFT"
        }

        if(userMatch.match.distance - inMatchReq.distance <= 0 && !userMatch.finish){
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
        if(userMatch.userHistory.graph.size >= 2){
            userMatch.id?.let { userMatchRepository.findAllByMatch_Id(it).forEach { otherUserMatch ->
                if(otherUserMatch.id != userMatch.id && otherUserMatch.userHistory.graph.size >= 2){
                    val userGraphSize = userMatch.userHistory.graph.size
                    val otherUserGraphSize = otherUserMatch.userHistory.graph.size

                    if(userMatch.userHistory.graph[userGraphSize-2] < otherUserMatch.userHistory.graph[otherUserGraphSize-2]){
                        if(userMatch.userHistory.graph[userGraphSize-1] > otherUserMatch.userHistory.graph[otherUserGraphSize-1]){
                            inMatchRes.alarmCategory = "OVERTAKING"
                        }
                    }
                    else if(userMatch.userHistory.graph[userGraphSize-2] > otherUserMatch.userHistory.graph[otherUserGraphSize-2]){
                        if(userMatch.userHistory.graph[userGraphSize-1] < otherUserMatch.userHistory.graph[otherUserGraphSize-1]){
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
    }*/
}