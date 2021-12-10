package com.snucse.pacemaker.service.match

import com.snucse.pacemaker.domain.Match
import com.snucse.pacemaker.domain.MatchStatus
import com.snucse.pacemaker.domain.UserMatch
import com.snucse.pacemaker.repository.MatchRepository
import com.snucse.pacemaker.repository.UserMatchRepository
import com.snucse.pacemaker.repository.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import javax.transaction.Transactional

@SpringBootTest
//@Transactional
class MatchServiceImplTest(
        @Autowired private val userRepository: UserRepository,
        @Autowired private val matchRepository: MatchRepository,
        @Autowired private val userMatchRepository: UserMatchRepository
) {
//
//    @Test
//    fun addTestMatch(){
//
//        val user1 = userRepository.findById(1).orElseThrow()
//        val user2 = userRepository.findById(2).orElseThrow()
//
//        val match = Match(
//
//                matchStatus = MatchStatus.DONE,
//                matchStartDatetime = LocalDateTime.now().plusSeconds(-15),
//                matchEndDatetime = LocalDateTime.now().plusSeconds(15),
//                totalDistance = 1000,
//                totalMembers = 2
//        )
//
//        val userMatches = mutableListOf<UserMatch>()
//
//        userMatches.add(UserMatch(
//
//                user = user1,
//                match = match,
//                currentSpeed = 30.0,
//                currentDistance = 1000,
//
//                rank = 1,
//                maximumSpeed = 40.0,
//
//                graph = "0",
//                left50 = false,
//                left100 = false,
//                finish = true,
//                finishOther = false
//        ))
//
//        userMatches.add(UserMatch(
//
//                user = user2,
//                match = match,
//                currentSpeed = 30.0,
//                currentDistance = 1000,
//
//                rank = 2,
//                maximumSpeed = 40.0,
//
//                graph = "0",
//                left50 = false,
//                left100 = false,
//                finish = false,
//                finishOther = true
//        ))
//
//        val savedMatch = matchRepository.save(match)
//        userMatchRepository.saveAll(userMatches)
//
//        val userHistories = userMatches.map { userMatch ->
//            userMatch.toUserHistoryDto()
//        }
//
//        assertEquals(match.id, savedMatch.id)
//    }
}