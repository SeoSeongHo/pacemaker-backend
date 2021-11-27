package com.snucse.pacemaker.dto

import com.snucse.pacemaker.domain.Match
import com.snucse.pacemaker.domain.MatchStatus
import com.snucse.pacemaker.domain.User
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.EnumType
import javax.persistence.Enumerated

class MatchDto {

    data class MatchReq(
            // 거리
            var distance: Long,
            // 참가자 수
            var participants: Long
    )

    data class MatchRes(
            @Enumerated(EnumType.STRING)
            var status: MatchStatus,
            var startDatetime: LocalDateTime,
            var users: List<UserDto.UserRes>?
    )
}