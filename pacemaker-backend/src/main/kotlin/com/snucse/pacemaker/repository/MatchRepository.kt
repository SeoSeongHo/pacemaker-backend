package com.snucse.pacemaker.repository

import com.snucse.pacemaker.domain.Match
import org.springframework.data.jpa.repository.JpaRepository

interface MatchRepository: JpaRepository<Match, Long> {
}