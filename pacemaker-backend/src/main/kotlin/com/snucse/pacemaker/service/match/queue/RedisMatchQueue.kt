package com.snucse.pacemaker.service.match.queue

import com.snucse.pacemaker.domain.Match
import com.snucse.pacemaker.domain.User
import java.util.*

class RedisMatchQueue {

    companion object{

        var matchQueues = mutableMapOf<String, Queue<Long>>()

        fun add(category: String, userId: Long){
            if(!isExistUser(category, userId)){
                if(matchQueues[category] != null){
                    matchQueues[category]!!.add(userId)
                }
                else{
                    matchQueues[category] = LinkedList(listOf(userId))
                }
            }
        }

        // retrieves and remove
        fun poll(category: String): Long?{
            return if(matchQueues[category] == null){
                null
            } else{
                matchQueues[category]!!.poll()
            }
        }

        // just retrieves
        fun peek(category: String): Long?{
            return if(matchQueues[category] == null){
                null
            } else{
                matchQueues[category]!!.peek()
            }
        }

        fun remove(category: String, userId: Long){
            if(matchQueues[category] != null){
                matchQueues[category]!!.remove(userId)
            }
        }

        fun isExistUser(category: String, userId: Long): Boolean{
            if(matchQueues[category] != null){
                return matchQueues[category]!!.contains(userId)
            }

            return false
        }
    }
}