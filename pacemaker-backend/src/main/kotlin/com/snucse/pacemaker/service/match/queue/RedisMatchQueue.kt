package com.snucse.pacemaker.service.match.queue

import com.snucse.pacemaker.domain.Match
import com.snucse.pacemaker.domain.User
import java.util.*

class RedisMatchQueue {

    companion object{

        var matchQueue: Queue<Long> = LinkedList<Long>()

        fun add(userId: Long){
            matchQueue.add(userId)
        }

        fun poll(): Long{
            return matchQueue.poll()
        }

        fun peek(userId: Long){
            matchQueue.peek()
        }
    }
}