package com.snucse.pacemaker.service.match

import com.snucse.pacemaker.repository.MatchRepository
import com.snucse.pacemaker.service.match.queue.RedisMatchQueue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.cache.CacheProperties

class MatchServiceImpl(
        @Autowired private val matchRepository: MatchRepository
): MatchService {

    fun match(category: String, userId: Long){

        // If it already exists in the queue
        if(RedisMatchQueue.isExistUser(category, userId)){
            // Check Status
            var match = matchRepository.find()
        }
        else{
            RedisMatchQueue.add(category, userId)
        }
    }
}