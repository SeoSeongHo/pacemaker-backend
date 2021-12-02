package com.snucse.pacemaker.service.match.consumer

import com.snucse.pacemaker.domain.Match
import com.snucse.pacemaker.domain.User
import com.snucse.pacemaker.domain.UserMatch
import com.snucse.pacemaker.repository.MatchRepository
import com.snucse.pacemaker.repository.UserMatchRepository
import com.snucse.pacemaker.repository.UserRepository
import com.snucse.pacemaker.service.match.queue.RedisMatchQueue
import org.apache.tomcat.jni.Local
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.lang.Exception
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.transaction.Transactional

@Service
@Transactional
class MatchQueueConsumerImpl(
        @Autowired private val userMatchRepository: UserMatchRepository,
        @Autowired private val matchRepository: MatchRepository,
        @Autowired private val userRepository: UserRepository
): MatchQueueConsumer {

    private val distances = arrayListOf<Long>(1000, 1500, 2000)
    private val participantNumbers = arrayListOf<Long>(2, 3, 4)

    fun consumeMatchFromQueue(){

        var category = ""

        for(participantNumber in participantNumbers){
            for(distance in distances){
                category = "${distance}_${participantNumber}"
                poll(category, participantNumber)
            }
        }

    }

    fun poll(category: String, participantsNumber: Long){

        if (RedisMatchQueue.matchQueues[category] != null && RedisMatchQueue.matchQueues[category]!!.size >= participantsNumber)
        {

            val matchUsers = mutableListOf<User>()
            for(i in 0 until participantsNumber){
                val user = userRepository.findById(RedisMatchQueue.matchQueues[category]!!.poll()).orElseThrow()
                if(user != null){
                    matchUsers.add(user)
                }
            }

            val now = LocalDateTime.now().plusSeconds(5)
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val dateFrom = formatter.format(now)

            val match = Match(matchStartDatetime = LocalDateTime.parse(dateFrom, formatter))
            val savedMatch = matchRepository.save(match)

            for(matchUser in matchUsers){
                val userMatch = UserMatch(
                        user = matchUser,
                        match = savedMatch
                )
                userMatchRepository.save(userMatch)
            }
        }
    }

    override fun consume() {

        val timer = Timer()
        var timerCnt = 0

        timer.schedule(object: TimerTask(){
            override fun run(){
                //println("$timerCnt")
                consumeMatchFromQueue()
                timerCnt++
                if(timerCnt > 60*20){
                    timer.cancel()
                }
            }
        }, 1000, 1000)
    }

//    fun poll_2(category: String) {
//        if (RedisMatchQueue.matchQueues[category] != null
//                && RedisMatchQueue.matchQueues[category]!!.size >= 2) {
//
//            val user1 = userRepository.findById(RedisMatchQueue.matchQueues[category]!!.poll()).orElseThrow()
//            val user2 = userRepository.findById(RedisMatchQueue.matchQueues[category]!!.poll()).orElseThrow()
//
//            var now = LocalDateTime.now().plusSeconds(5)
//            var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
//            var text = formatter.format(now)
//
//            val match = Match(
//                    matchStartDatetime = LocalDateTime.parse(text, formatter)
//            )
//
//            val savedMatch = matchRepository.save(match)
//
//            val userMatch1 = UserMatch(
//                    user = user1,
//                    match = savedMatch
//            )
//
//            val userMatch2 = UserMatch(
//                    user = user2,
//                    match = savedMatch
//            )
//
//            userMatchRepository.save(userMatch1)
//            userMatchRepository.save(userMatch2)
//        }
//    }

//    fun poll_3(category: String) {
//        if (RedisMatchQueue.matchQueues[category] != null
//                && RedisMatchQueue.matchQueues[category]!!.size >= 3) {
//
//            val user1 = userRepository.findById(RedisMatchQueue.matchQueues[category]!!.poll()).orElseThrow()
//            val user2 = userRepository.findById(RedisMatchQueue.matchQueues[category]!!.poll()).orElseThrow()
//            val user3 = userRepository.findById(RedisMatchQueue.matchQueues[category]!!.poll()).orElseThrow()
//
//            var now = LocalDateTime.now().plusSeconds(5)
//            var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
//            var text = formatter.format(now)
//
//            val match = Match(
//                    matchStartDatetime = LocalDateTime.parse(text, formatter)
//            )
//
//            val savedMatch = matchRepository.save(match)
//
//            val userMatch1 = UserMatch(
//                    user = user1,
//                    match = savedMatch
//            )
//
//            val userMatch2 = UserMatch(
//                    user = user2,
//                    match = savedMatch
//            )
//
//            val userMatch3 = UserMatch(
//                    user = user3,
//                    match = savedMatch
//            )
//
//            userMatchRepository.save(userMatch1)
//            userMatchRepository.save(userMatch2)
//            userMatchRepository.save(userMatch3)
//        }
//    }
//
//    fun poll_4(category: String) {
//        if (RedisMatchQueue.matchQueues[category] != null
//                && RedisMatchQueue.matchQueues[category]!!.size >= 4) {
//
//            val user1 = userRepository.findById(RedisMatchQueue.matchQueues[category]!!.poll()).orElseThrow()
//            val user2 = userRepository.findById(RedisMatchQueue.matchQueues[category]!!.poll()).orElseThrow()
//            val user3 = userRepository.findById(RedisMatchQueue.matchQueues[category]!!.poll()).orElseThrow()
//            val user4 = userRepository.findById(RedisMatchQueue.matchQueues[category]!!.poll()).orElseThrow()
//
//            var now = LocalDateTime.now().plusSeconds(5)
//            var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
//            var text = formatter.format(now)
//
//            val match = Match(
//                    matchStartDatetime = LocalDateTime.parse(text, formatter)
//            )
//
//            val savedMatch = matchRepository.save(match)
//
//            val userMatch1 = UserMatch(
//                    user = user1,
//                    match = savedMatch
//            )
//
//            val userMatch2 = UserMatch(
//                    user = user2,
//                    match = savedMatch
//            )
//
//            val userMatch3 = UserMatch(
//                    user = user3,
//                    match = savedMatch
//            )
//
//            val userMatch4 = UserMatch(
//                    user = user4,
//                    match = savedMatch
//            )
//
//            userMatchRepository.save(userMatch1)
//            userMatchRepository.save(userMatch2)
//            userMatchRepository.save(userMatch3)
//            userMatchRepository.save(userMatch4)
//        }
//    }
}