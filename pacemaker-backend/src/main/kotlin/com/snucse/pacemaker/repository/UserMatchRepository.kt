package com.snucse.pacemaker.repository

import com.snucse.pacemaker.domain.UserMatch
import org.springframework.data.jpa.repository.JpaRepository

interface UserMatchRepository: JpaRepository<UserMatch, Long> {

    fun existsByUser_Id(userId: Long): Boolean
    fun findByUser_Id(userId: Long): List<UserMatch>?
}