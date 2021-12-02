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
import java.time.format.DateTimeFormatter

@Service
@Transactional
class MatchServiceImpl(
        @Autowired private val userMatchRepository: UserMatchRepository,
        @Autowired private val matchRepository: MatchRepository
): MatchService {

    override fun match(matchReq: MatchDto.MatchReq, userId: Long): MatchDto.MatchRes {

        val category = "${matchReq.distance}_${matchReq.participants}"

        val now = LocalDateTime.now().plusSeconds(5)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val text = formatter.format(now)

        // If it already exists in the match queue
        if(RedisMatchQueue.isExistUser(category, userId)){

            // If it exists in the match DB
            if(userMatchRepository.existsByUser_Id(userId)){
                val userMatches = userMatchRepository.findByUser_Id(userId)
                // find last user match
                userMatches!!.filter { userMatch ->  userMatch.match.matchStatus == MatchStatus.MATCHING_COMPLETE}
                userMatches.sortedByDescending { userMatch ->  userMatch.match.matchEndDatetime }
                val match = userMatches[0].match

                val realUserMatches = userMatchRepository.findAllByMatch_Id(match.id!!)
                if(realUserMatches.isNotEmpty()){

                    realUserMatches.map { realUserMatch -> realUserMatch.match.matchStatus == MatchStatus.RUNNING }
                }

                userMatchRepository.saveAll(realUserMatches)

                val users = mutableListOf<User>()
                if(realUserMatches.isNotEmpty()){
                    realUserMatches.forEach { userMatch ->
                        users.add(userMatch.user)
                    }
                }

                val mainUser = users.filter { user -> user.id == userId }
                users.add(0, mainUser[0])
                users.toList().sortedBy { user -> user.id }

                return MatchDto.MatchRes(
                        status = MatchStatus.RUNNING,
                        startDatetime = LocalDateTime.parse(text, formatter),
                        users = users.map { user -> MatchDto.MatchUser(
                                id = user.id!!,
                                email =  user.email,
                                nickname = user.nickname) }
                )
            }
        }
        else{
            if(!RedisMatchQueue.isExistUser(category, userId)){
                RedisMatchQueue.add(category, userId)
            }
        }

        return MatchDto.MatchRes(
                status = MatchStatus.MATCHING,
                startDatetime = LocalDateTime.parse(text, formatter),
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
                        id = otherUserMatch.user.id!!,
                        email =  otherUserMatch.user.email,
                        nickname = otherUserMatch.user.nickname,
                        currentDistance = otherUserMatch.currentDistance,
                        currentSpeed = otherUserMatch.currentSpeed
                ))
            }
        } }

        if(userMatch.match.totalDistance - inMatchReq.currentDistance <= 100 && !userMatch.left100){
            userMatch.left100 = true
            inMatchRes.alarmCategory = "LEFT_100M"
        }

        if(userMatch.match.totalDistance - inMatchReq.currentDistance <= 50 && !userMatch.left50){
            userMatch.left50 = true
            inMatchRes.alarmCategory = "LEFT_50M"
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
            val match = userMatch.match
            match.matchStatus = MatchStatus.RUNNING_COMPLETE
            matchRepository.save(match)
        }

        return inMatchRes
    }
}