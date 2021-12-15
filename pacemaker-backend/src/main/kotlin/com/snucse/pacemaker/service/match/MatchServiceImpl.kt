package com.snucse.pacemaker.service.match

import com.snucse.pacemaker.domain.MatchStatus
import com.snucse.pacemaker.domain.User
import com.snucse.pacemaker.domain.UserMatch
import com.snucse.pacemaker.dto.MatchDto
import com.snucse.pacemaker.dto.UserDto
import com.snucse.pacemaker.exception.UserMatchNotExistException
import com.snucse.pacemaker.repository.MatchRepository
import com.snucse.pacemaker.repository.UserMatchRepository
import com.snucse.pacemaker.service.match.queue.RedisMatchQueue
import org.springframework.beans.factory.annotation.Autowired
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

    override fun cancelInMatch(userMatchId: Long){

        val now = LocalDateTime.now().plusHours(9)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dateForm = formatter.format(now)

        val userMatch = userMatchRepository.findById(userMatchId).orElseThrow()
        val match = userMatch.match
        match.matchStatus = MatchStatus.NONE
        match.matchEndDatetime = LocalDateTime.parse(dateForm, formatter)
        matchRepository.save(match)
    }

    override fun getUserMatchHistory(userMatchId: Long): UserDto.UserHistory{

        val userMatch = userMatchRepository.getById(userMatchId)
        return userMatch.toUserHistoryDto()
    }

    override fun cancelMatch(matchReq: MatchDto.MatchReq, userId: Long){

        val category = "${matchReq.distance}_${matchReq.participants}"

        // If it already exists in the match queue
        if(RedisMatchQueue.isExistUser(category, userId)){
            RedisMatchQueue.remove(category, userId)
        }
    }

    override fun match(matchReq: MatchDto.MatchReq, userId: Long): MatchDto.MatchRes {

        val category = "${matchReq.distance}_${matchReq.participants}"

        // If exists user match by user id
        if(userMatchRepository.existsByUser_Id(userId)){
            val userMatches = userMatchRepository.findByUser_Id(userId)
            // Find completed match by order descending
            val completedUserMatches = userMatches!!.filter { userMatch ->  userMatch.match.matchStatus == MatchStatus.MATCHING_COMPLETE }.sortedByDescending { userMatch -> userMatch.match.matchEndDatetime }

            // If not exists completed match
            if(completedUserMatches.count() < 1){
                // If exists data in queue, should skip
                if(RedisMatchQueue.isExistUser(category, userId)){
                    // skip
                }
                // Else append data to queue
                else{
                    RedisMatchQueue.add(category, userId)
                }
            }
            // If exists completed match
            else{
                // Find last user match
                val lastUserMatch = completedUserMatches[0]
                val match = lastUserMatch.match

                // If last match's start datetime is before the (now - 1 minute)
                if(match.matchStartDatetime!!.isBefore(LocalDateTime.now().plusHours(9).minusMinutes(1))){
                    // If exists data in queue, should skip
                    if(RedisMatchQueue.isExistUser(category, userId)){
                        // skip
                    }
                    // Else append data to queue
                    else{
                        RedisMatchQueue.add(category, userId)
                    }
                }
                // Else
                else{
                    // find real user match
                    val realUserMatches = userMatchRepository.findAllByMatch_Id(match.id!!)

                    // If real user match is not empty
                    if(realUserMatches.isNotEmpty()){
                        val filteredRealUserMatches = realUserMatches.filter { realUserMatch -> realUserMatch.match.matchStatus == MatchStatus.MATCHING_COMPLETE }

                        val users = mutableListOf<User>()
                        var matchUsers = mutableListOf<MatchDto.MatchUser>()
                        if(filteredRealUserMatches.isNotEmpty()){
                            filteredRealUserMatches.forEach { userMatch ->
                                matchUsers.add(MatchDto.MatchUser(
                                        id = userMatch.user.id!!,
                                        userMatchId = userMatch.id!!,
                                        email = userMatch.user.email,
                                        nickname = userMatch.user.nickname,
                                        currentDistance = userMatch.currentDistance,
                                        currentSpeed = userMatch.currentSpeed
                                ))
                                users.add(userMatch.user)
                            }
                        }

                        val mainMatchUsers = matchUsers.filter { matchUser -> matchUser.id == userId }.toMutableList()
                        matchUsers = matchUsers.filter { matchUser -> matchUser.id != userId }.toMutableList()
                        matchUsers.add(0, mainMatchUsers[0])

                        return MatchDto.MatchRes(
                                status = MatchStatus.MATCHING_COMPLETE,
                                startDatetime = match.matchStartDatetime!!,
                                users = matchUsers
                        )
                    }
                    // Else if user match is not exists
                    throw Exception("woooooooo!!!! exception occurred!!")
                }
            }
        }
        // If not exists in user match db
        else{
            // If already exists in queue, should skip
            if(RedisMatchQueue.isExistUser(category, userId)){
                // skip
            }
            // Else append queue
            else{
                    RedisMatchQueue.add(category, userId)
            }
        }

        // If match is processing, return response with MatchStatus.MATCHING
        val now = LocalDateTime.now().plusHours(9).plusSeconds(15)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dateForm = formatter.format(now)

        return MatchDto.MatchRes(
                status = MatchStatus.MATCHING,
                startDatetime = LocalDateTime.parse(dateForm, formatter),
                users = mutableListOf()
        )
    }

    override fun getUserMatchByUserMatchId(userMatchId: Long): UserMatch =
            userMatchRepository.findById(userMatchId).orElseThrow { throw UserMatchNotExistException("can't find userMatch by id: $userMatchId.") }

    override fun inMatchPolling(inMatchReq: MatchDto.InMatchReq): MatchDto.InMatchRes {

        val userMatch = getUserMatchByUserMatchId(inMatchReq.userMatchId)
        val match = userMatch.match

        if(match.matchStatus == MatchStatus.DONE){
            return MatchDto.InMatchRes(
                    matchUsers = mutableListOf(MatchDto.MatchUser(
                            id = userMatch.user.id!!,
                            email =  userMatch.user.email,
                            nickname = userMatch.user.nickname,
                            currentDistance = userMatch.currentDistance,
                            currentSpeed = userMatch.currentSpeed,
                            userMatchId = userMatch.id!!
                    )),
                    alarmCategory = "DONE"
            )
        }

        if(match.matchStatus != MatchStatus.MATCHING && match.matchStatus != MatchStatus.MATCHING_COMPLETE){
            return MatchDto.InMatchRes(
                    matchUsers = mutableListOf(MatchDto.MatchUser(
                            id = userMatch.user.id!!,
                            email =  userMatch.user.email,
                            nickname = userMatch.user.nickname,
                            currentDistance = userMatch.currentDistance,
                            currentSpeed = userMatch.currentSpeed,
                            userMatchId = userMatch.id!!
                    )),
                    alarmCategory = "CANCEL"
            )
        }

        if(match.totalDistance!!.toDouble() >= userMatch.currentDistance)
        {
            if(inMatchReq.count % 7 == 1){
                // Update graph
                userMatch.graph += ";" + inMatchReq.currentSpeed.toString()
            }

            // Update distance
            userMatch.currentDistance = inMatchReq.currentDistance

            // Update speed
            if(userMatch.maximumSpeed < inMatchReq.currentSpeed){
                userMatch.maximumSpeed = inMatchReq.currentSpeed
            }
            userMatch.currentSpeed = inMatchReq.currentSpeed
        }

        val inMatchRes = MatchDto.InMatchRes(
                matchUsers = mutableListOf(),
                alarmCategory = "NONE"
        )

        val matchUsers = userMatchRepository.findAllByMatch_Id(match.id!!)
        val mainMatchUsers = matchUsers.filter { matchUser -> matchUser.id == userMatch.id }.toMutableList()
        val orderedMatchUsers = matchUsers.filter { matchUser -> matchUser.id != userMatch.id }.toMutableList()
        orderedMatchUsers.add(0, mainMatchUsers[0])

        orderedMatchUsers.map { orderedMatchUser ->
            inMatchRes.matchUsers.add(MatchDto.MatchUser(
                    id = orderedMatchUser.user.id!!,
                    email =  orderedMatchUser.user.email,
                    nickname = orderedMatchUser.user.nickname,
                    currentDistance = orderedMatchUser.currentDistance,
                    currentSpeed = orderedMatchUser.currentSpeed,
                    userMatchId = orderedMatchUser.id!!
            ))
        }

        val now = LocalDateTime.now().plusHours(9)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dateForm = formatter.format(now)

        // Finish
        if(!userMatch.finish && (userMatch.match.totalDistance!!.toDouble() <= inMatchReq.currentDistance) && !userMatch.finish){
            userMatch.finish = true
            inMatchRes.alarmCategory = "FINISH"
        }

        if(userMatch.finish){

            var done = true
            var rank = 1

            val otherUserMatches = userMatchRepository.findAllByMatch_Id(match.id!!).filter { findUserMatch -> findUserMatch.id != userMatch.id }
            otherUserMatches.forEach{ otherUserMatch ->
                if(!otherUserMatch.finish){
                    done = false
                }
                else{
                    rank += 1
                }
            }

            userMatch.rank = rank

            if(done){
                inMatchRes.alarmCategory = "DONE"
                match.matchStatus = MatchStatus.DONE
                match.matchEndDatetime = LocalDateTime.parse(dateForm, formatter)
                matchRepository.save(match)
                return inMatchRes
            }

            return inMatchRes
        }

        // Left 50m
        if(userMatch.match.totalDistance!!.toDouble() - inMatchReq.currentDistance <= 50 && !userMatch.left50){
            userMatch.left50 = true
            inMatchRes.alarmCategory = "LEFT_50M"
            userMatchRepository.save(userMatch)

            return inMatchRes
        }

        // Left 100m
        if((userMatch.match.totalDistance!!.toDouble() - inMatchReq.currentDistance) <= 100 && !userMatch.left100){
            userMatch.left100 = true
            inMatchRes.alarmCategory = "LEFT_100M"
            userMatchRepository.save(userMatch)

            return inMatchRes
        }

        return inMatchRes
    }
}