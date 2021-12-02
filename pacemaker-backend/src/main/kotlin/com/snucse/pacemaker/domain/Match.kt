package com.snucse.pacemaker.domain

import com.snucse.pacemaker.dto.MatchDto
import com.snucse.pacemaker.dto.UserDto
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.persistence.*
import kotlin.collections.ArrayList

@Entity
@Table(name = "match")
data class Match(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        var totalDistance: Long = 0,
        var totalMembers: Long = 0,
        var matchStartDatetime: LocalDateTime? = null,
        var matchEndDatetime: LocalDateTime? = null,
        @Enumerated(EnumType.STRING)
        var matchStatus: MatchStatus? = MatchStatus.MATCHING,
)

@Entity
@Table(name = "match_user")
data class UserMatch(

        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id")
        var user: User,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "match_id")
        var match: Match,

        var currentDistance: Long = 0,
        var currentSpeed: Long = 0,

        var rank: Long = 0,
        var maximumSpeed: Long = 0,

        var graph: String = "0",

        var left100: Boolean = false,
        var left50: Boolean = false,
        var finish: Boolean = false,
        var finishOther: Boolean = false,
) {
        fun toDto(): UserDto.UserHistory {
                return UserDto.UserHistory(
                        id = id!!,
                        totalDistance = match.totalDistance,

                        matchStartDatetime = match
                                .matchStartDatetime
                                !!.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),

                        matchEndDatetime = match
                                .matchEndDatetime
                                !!.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),

                        totalTime = Duration.between(match.matchStartDatetime, match.matchEndDatetime).seconds,

                        rank = rank,
                        totalMembers = match.totalMembers,

                        maximumSpeed = maximumSpeed,
                        graph = getGraph()
                )
        }

        fun getGraph(): ArrayList<Long> {
                val g = arrayListOf<Long>();
                graph.split(";").forEach {
                        g.add(it.toLong())
                }
                return g
        }


}

enum class MatchStatus{
        MATCHING, MATCHING_COMPLETE, DONE, ERROR
}