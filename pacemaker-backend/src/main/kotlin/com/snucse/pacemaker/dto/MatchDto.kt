package com.snucse.pacemaker.dto

import com.snucse.pacemaker.domain.User

class MatchDto {

    data class InMatchReq(
            val distance: Long,
            val speed: Long
    )

    data class InMatchRes(
            val matchUsers: MutableList<MatchUser>,
            var alarmCategory: String
    )

    data class MatchUser(
            val email: String,
            val nickname: String,
            val distance: Long,
            val speed: Long
    )
}