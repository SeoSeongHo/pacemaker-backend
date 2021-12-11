package com.snucse.pacemaker.service.match

import com.snucse.pacemaker.domain.Match
import com.snucse.pacemaker.domain.UserMatch

import com.snucse.pacemaker.dto.MatchDto
import com.snucse.pacemaker.dto.UserDto

interface MatchService {

    fun match(matchReq: MatchDto.MatchReq, userId: Long): MatchDto.MatchRes
    fun cancelMatch(matchReq: MatchDto.MatchReq, userId: Long)
    fun getUserMatchByUserMatchId(userMatchId: Long): UserMatch
    fun inMatchPolling(inMatchReq: MatchDto.InMatchReq): MatchDto.InMatchRes
    fun getUserMatchHistory(userMatchId: Long): UserDto.UserHistory
    fun cancelInMatch(matchId: Long)
}