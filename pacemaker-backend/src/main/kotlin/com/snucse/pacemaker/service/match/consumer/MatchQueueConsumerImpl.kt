package com.snucse.pacemaker.service.match.consumer

import com.snucse.pacemaker.domain.Match
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
import javax.transaction.Transactional

@Service
@Transactional
class MatchQueueConsumerImpl(
        @Autowired private val userMatchRepository: UserMatchRepository,
        @Autowired private val matchRepository: MatchRepository,
        @Autowired private val userRepository: UserRepository
): MatchQueueConsumer {

    private val distances = arrayListOf<Long>(1000, 1500, 2000)
    private val participants = arrayListOf<Long>(2, 3, 4)

    override fun consume() {
        var category = ""

        // 1000_2
        category = "${distances[0]}_${participants[0]}"
        poll_2(category)

        // 1000_3
        category = "${distances[0]}_${participants[1]}"
        poll_3(category)

        // 1000_4
        category = "${distances[0]}_${participants[2]}"
        poll_4(category)

        // 1500_2
        category = "${distances[1]}_${participants[0]}"
        poll_2(category)

        // 1500_3
        category = "${distances[1]}_${participants[1]}"
        poll_3(category)

        // 1500_4
        category = "${distances[1]}_${participants[2]}"
        poll_4(category)

        // 2000_2
        category = "${distances[2]}_${participants[0]}"
        poll_2(category)

        // 2000_3
        category = "${distances[2]}_${participants[1]}"
        poll_3(category)

        // 2000_4
        category = "${distances[2]}_${participants[2]}"
        poll_4(category)
    }

    fun poll_2(category: String) {
        if (RedisMatchQueue.matchQueues[category] != null
                && RedisMatchQueue.matchQueues[category]!!.size >= 2) {

            val user1 = userRepository.findById(RedisMatchQueue.matchQueues[category]!!.poll()).orElseThrow()
            val user2 = userRepository.findById(RedisMatchQueue.matchQueues[category]!!.poll()).orElseThrow()

            var now = LocalDateTime.now().plusSeconds(5)
            var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            var text = formatter.format(now)

            val match = Match(
                    matchStartDatetime = LocalDateTime.parse(text, formatter)
            )

            val savedMatch = matchRepository.save(match)

            val userMatch1 = UserMatch(
                    user = user1,
                    match = savedMatch
            )

            val userMatch2 = UserMatch(
                    user = user2,
                    match = savedMatch
            )

            userMatchRepository.save(userMatch1)
            userMatchRepository.save(userMatch2)
        }
    }

    fun poll_3(category: String) {
        if (RedisMatchQueue.matchQueues[category] != null
                && RedisMatchQueue.matchQueues[category]!!.size >= 3) {

            val user1 = userRepository.findById(RedisMatchQueue.matchQueues[category]!!.poll()).orElseThrow()
            val user2 = userRepository.findById(RedisMatchQueue.matchQueues[category]!!.poll()).orElseThrow()
            val user3 = userRepository.findById(RedisMatchQueue.matchQueues[category]!!.poll()).orElseThrow()

            var now = LocalDateTime.now().plusSeconds(5)
            var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            var text = formatter.format(now)

            val match = Match(
                    matchStartDatetime = LocalDateTime.parse(text, formatter)
            )

            val savedMatch = matchRepository.save(match)

            val userMatch1 = UserMatch(
                    user = user1,
                    match = savedMatch
            )

            val userMatch2 = UserMatch(
                    user = user2,
                    match = savedMatch
            )

            val userMatch3 = UserMatch(
                    user = user3,
                    match = savedMatch
            )

            userMatchRepository.save(userMatch1)
            userMatchRepository.save(userMatch2)
            userMatchRepository.save(userMatch3)
        }
    }

    fun poll_4(category: String) {
        if (RedisMatchQueue.matchQueues[category] != null
                && RedisMatchQueue.matchQueues[category]!!.size >= 4) {

            val user1 = userRepository.findById(RedisMatchQueue.matchQueues[category]!!.poll()).orElseThrow()
            val user2 = userRepository.findById(RedisMatchQueue.matchQueues[category]!!.poll()).orElseThrow()
            val user3 = userRepository.findById(RedisMatchQueue.matchQueues[category]!!.poll()).orElseThrow()
            val user4 = userRepository.findById(RedisMatchQueue.matchQueues[category]!!.poll()).orElseThrow()

            var now = LocalDateTime.now().plusSeconds(5)
            var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            var text = formatter.format(now)

            val match = Match(
                    matchStartDatetime = LocalDateTime.parse(text, formatter)
            )

            val savedMatch = matchRepository.save(match)

            val userMatch1 = UserMatch(
                    user = user1,
                    match = savedMatch
            )

            val userMatch2 = UserMatch(
                    user = user2,
                    match = savedMatch
            )

            val userMatch3 = UserMatch(
                    user = user3,
                    match = savedMatch
            )

            val userMatch4 = UserMatch(
                    user = user4,
                    match = savedMatch
            )

            userMatchRepository.save(userMatch1)
            userMatchRepository.save(userMatch2)
            userMatchRepository.save(userMatch3)
            userMatchRepository.save(userMatch4)
        }
    }
}