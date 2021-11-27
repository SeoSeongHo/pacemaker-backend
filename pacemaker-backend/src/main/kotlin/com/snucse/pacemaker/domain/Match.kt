package com.snucse.pacemaker.domain

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "match")
data class Match(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        @OneToMany(mappedBy = "match", fetch = FetchType.LAZY)
        var users: MutableList<User> = mutableListOf(),

        @OneToMany(mappedBy = "match", fetch = FetchType.LAZY)
        var matchUsers: MutableList<MatchUser> = mutableListOf(),
        var matchStartDatetime: LocalDateTime,
        var matchEndDatetime: LocalDateTime,

        @Enumerated(EnumType.STRING)
        var status: MatchStatus
) {
}

enum class MatchStatus{
    Pending, Processing, Success, ERROR
}

@Entity
@Table(name = "match_user")
data class MatchUser(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        var distance: Long,
        var speed: Long
){

}