package com.snucse.pacemaker.domain

import com.snucse.pacemaker.dto.UserDto
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.persistence.*
import javax.validation.constraints.Size

@Entity
data class UserMatch(

        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id")
        var user: User,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "match_id")
        var match: Match,

        var currentDistance: Double = 0.0,
        var currentSpeed: Double = 0.0,

        @Column(name = "match_rank")
        var rank: Int = 0,
        var maximumSpeed: Double = 0.0,

        @Size(max = 1000)
        var graph: String = "0.0",

        var left100: Boolean = false,
        var left50: Boolean = false,
        var finish: Boolean = false,
        var finishOther: Boolean = false,
) {
    fun toUserHistoryDto(): UserDto.UserHistory {

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val matchStartDatetime = formatter.format(match.matchStartDatetime!!)
        val matchEndDatetime = formatter.format(match.matchEndDatetime!!)

        return UserDto.UserHistory(
                id = id!!,
                totalDistance = match.totalDistance!!,
                matchStartDatetime = LocalDateTime.parse(matchStartDatetime, formatter),
                matchEndDatetime = LocalDateTime.parse(matchEndDatetime, formatter),
                totalTime = Duration.between(match.matchStartDatetime, match.matchEndDatetime).seconds,
                rank = rank,
                totalMembers = match.totalMembers!!,
                maximumSpeed = maximumSpeed,
                graph = getGraph()
        )
    }

    fun getGraph(): ArrayList<Double> {
        val g = arrayListOf<Double>();
        graph.split(";").forEach {
            g.add(it.toDouble())
        }
        return g
    }
}