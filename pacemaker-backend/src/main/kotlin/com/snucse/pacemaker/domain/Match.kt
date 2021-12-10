package com.snucse.pacemaker.domain

import com.snucse.pacemaker.dto.UserDto
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.persistence.*
import javax.validation.constraints.Size
import kotlin.math.max

@Entity
@Table(name = "matches")
data class Match(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        var totalDistance: Long? = 0,
        var totalMembers: Long? = 0,
        var matchStartDatetime: LocalDateTime? = null,
        var matchEndDatetime: LocalDateTime? = null,
        @Enumerated(EnumType.STRING)
        var matchStatus: MatchStatus? = MatchStatus.MATCHING,
)

enum class MatchStatus{
        MATCHING, MATCHING_COMPLETE, DONE, ERROR, NONE
}