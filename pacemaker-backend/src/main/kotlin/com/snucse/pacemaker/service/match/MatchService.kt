package com.snucse.pacemaker.service.match

import com.snucse.pacemaker.domain.Match
import com.snucse.pacemaker.domain.UserMatch

import com.snucse.pacemaker.dto.MatchDto

interface MatchService {

    fun match(matchReq: MatchDto.MatchReq, userId: Long): MatchDto.MatchRes

    fun getUserMatchByUserMatchId(userMatchId: Long): UserMatch
    fun inMatchPolling(inMatchReq: MatchDto.InMatchReq): MatchDto.InMatchRes

}