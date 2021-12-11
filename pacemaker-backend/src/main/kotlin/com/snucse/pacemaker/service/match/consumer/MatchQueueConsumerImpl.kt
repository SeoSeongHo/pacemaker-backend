package com.snucse.pacemaker.service.match.consumer

import com.snucse.pacemaker.domain.Match
import com.snucse.pacemaker.domain.MatchStatus
import com.snucse.pacemaker.domain.User
import com.snucse.pacemaker.domain.UserMatch
import com.snucse.pacemaker.repository.MatchRepository
import com.snucse.pacemaker.repository.UserMatchRepository
import com.snucse.pacemaker.repository.UserRepository
import com.snucse.pacemaker.service.match.queue.RedisMatchQueue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
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

        for(participantNumber in participantNumbers){
            for(distance in distances){
                poll(distance, participantNumber)
            }
        }
    }

    fun poll(distance: Long, participantsNumber: Long){

        val category = "${distance}_${participantsNumber}"

        if (RedisMatchQueue.matchQueues[category] != null && RedisMatchQueue.matchQueues[category]!!.size >= participantsNumber)
        {

            val users = mutableListOf<User>()
            for(i in 0 until participantsNumber){
                val user = userRepository.findById(RedisMatchQueue.matchQueues[category]!!.poll()).orElseThrow()
                if(user != null){
                    users.add(user)
                }
            }

            val now = LocalDateTime.now().plusSeconds(15)
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val dateForm = formatter.format(now)

            val match = Match(
                    matchStartDatetime = LocalDateTime.parse(dateForm, formatter),
                    matchStatus = MatchStatus.MATCHING_COMPLETE,
                    totalMembers = participantsNumber,
                    totalDistance = distance
            )

            val savedMatch = matchRepository.save(match)

            for(user in users){
                val userMatch = UserMatch(
                        user = user,
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
                consumeMatchFromQueue()
//                timerCnt++
//                if(timerCnt > 60*20){
//                    timer.cancel()
//                }
            }
        }, 1000, 1000)
    }
}