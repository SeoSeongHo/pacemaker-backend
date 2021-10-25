package com.snucse.pacemaker.repository

import com.snucse.pacemaker.domain.BlackList
import org.springframework.data.jpa.repository.JpaRepository

interface BlackListRepository: JpaRepository<BlackList, Long> {
    fun existsByToken(token: String): Boolean
}