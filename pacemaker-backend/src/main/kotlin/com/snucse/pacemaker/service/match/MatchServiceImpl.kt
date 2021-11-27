package com.snucse.pacemaker.service.match

import com.snucse.pacemaker.domain.Match
import com.snucse.pacemaker.domain.MatchStatus
import com.snucse.pacemaker.domain.User
import com.snucse.pacemaker.dto.MatchDto
import com.snucse.pacemaker.dto.UserDto
import com.snucse.pacemaker.exception.MatchingProcessingYetException
import com.snucse.pacemaker.repository.MatchRepository
import com.snucse.pacemaker.repository.UserMatchRepository
import com.snucse.pacemaker.service.match.queue.RedisMatchQueue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.cache.CacheProperties
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class MatchServiceImpl(
        @Autowired private val userMatchRepository: UserMatchRepository
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
}