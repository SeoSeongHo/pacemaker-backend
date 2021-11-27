package com.snucse.pacemaker.service.match

import com.snucse.pacemaker.domain.Match
import com.snucse.pacemaker.exception.MatchingProcessingYetException
import com.snucse.pacemaker.repository.MatchRepository
import com.snucse.pacemaker.repository.UserMatchRepository
import com.snucse.pacemaker.service.match.queue.RedisMatchQueue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.cache.CacheProperties

class MatchServiceImpl(
        @Autowired private val userMatchRepository: UserMatchRepository
): MatchService {

    override fun match(category: String, userId: Long): Match?{

        // If it already exists in the match queue
        if(RedisMatchQueue.isExistUser(category, userId)){

            // If it exists in the match DB
            if(userMatchRepository.existsByUser_Id(userId)){
                return userMatchRepository.findByUser_Id(userId)!!.match
            }
            else throw MatchingProcessingYetException("match is not processed yet.")
        }
        else{
            RedisMatchQueue.add(category, userId)
        }

        return null
    }
}