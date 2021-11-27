package com.snucse.pacemaker.service.match

import com.snucse.pacemaker.domain.Match
import com.snucse.pacemaker.dto.MatchDto.*

interface MatchService {

    fun match(category: String, userId: Long): Match?


    fun inMatchPolling(userId: Long, inMatchReq: InMatchReq): InMatchRes

}