package com.snucse.pacemaker.service.match

import com.snucse.pacemaker.domain.Match

interface MatchService {

    fun match(category: String, userId: Long): Match?
}