package com.snucse.pacemaker.domain

import com.snucse.pacemaker.dto.MatchDto
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "match")
data class Match(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        var distance: Long
        var matchStartDatetime: LocalDateTime? = null,
        var matchEndDatetime: LocalDateTime? = null
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

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "userHistory_id")
        var userHistory: UserHistory,

        var distance: Long,
        var speed: Long,

        var left100: Boolean = false,
        var left50: Boolean = false,
        var finish: Boolean = false,
        var finishOther: Boolean = false,
)

enum class MatchStatus{
        PENDING, DONE, ERROR
}