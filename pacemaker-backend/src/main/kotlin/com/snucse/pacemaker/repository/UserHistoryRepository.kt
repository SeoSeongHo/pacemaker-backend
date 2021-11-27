package com.snucse.pacemaker.repository

import com.snucse.pacemaker.domain.UserHistory
import com.snucse.pacemaker.domain.UserMatch
import org.springframework.data.jpa.repository.JpaRepository

interface UserHistoryRepository: JpaRepository<UserHistory, Long> {
    fun findAllByUser_Id(userId: Long): List<UserHistory>
}