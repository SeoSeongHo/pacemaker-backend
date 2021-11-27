package com.snucse.pacemaker.dto

import com.snucse.pacemaker.domain.User

class MatchDto {

    data class InMatchReq(
            val distance: Long,
            val speed: Long
    )

    data class InMatchRes(
            val matchUsers: MutableList<MatchUser>,
            val alarmCategory: String
    ){
        companion object{
            fun toDto(): InMatchRes {
                return InMatchRes(
                        matchUsers = mutableListOf<MatchUser>(),
                        alarmCategory = "test"
                )
            }
        }
    }

    data class MatchUser(
            val email: String,
            val nickname: String,
            val distance: Long,
            val speed: Long
    )
}