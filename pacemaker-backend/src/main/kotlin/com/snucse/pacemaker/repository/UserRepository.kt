package com.snucse.pacemaker.repository

import com.snucse.pacemaker.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, Long>