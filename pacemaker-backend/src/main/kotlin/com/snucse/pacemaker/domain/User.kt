package com.snucse.pacemaker.domain

import com.snucse.pacemaker.dto.UserDto
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter;
import javax.persistence.*

@Entity
@Table(name = "users")
data class User (
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        @Column(name = "email", unique = true, length = 200)
        var email: String,
        var password: String,

        @Column(name = "nickname", unique = true, length = 20)
        var nickname: String,
        var rating: String
) {
        fun isRightPassword(bCryptPasswordEncoder: BCryptPasswordEncoder, rawPassword: String): Boolean{
                return bCryptPasswordEncoder.matches(rawPassword, password)
        }

        fun updateNickname(nickname: String){
                this.nickname = nickname
        }

        fun toDto(): UserDto.UserRes {
                return UserDto.UserRes(
                        id = id!!,
                        email = email,
                        nickname = nickname,
                        rating = rating
                )
        }
}

@Entity
@Table(name = "userHistory")
data class UserHistory(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id")
        var user: User,

        var distance: Long,
        var matchStartDatetime: LocalDateTime,
        var matchEndDatetime: LocalDateTime,

        var rank: Long,
        var totalMembers: Long,

        var maximumVelocity: Long = 0,

        // distance list
        var graph: ArrayList<Long> = arrayListOf<Long>()

) {
        fun toDto(): UserDto.UserHistory {
                return UserDto.UserHistory(
                        id = id!!,
                        distance = distance,

                        matchStartDatetime = matchStartDatetime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss")),
                        matchEndDatetime = matchEndDatetime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss")),
                        time = Duration.between(matchStartDatetime, matchEndDatetime).seconds,

                        rank = rank,
                        totalMembers = totalMembers,

                        maximumVelocity = maximumVelocity,
                        graph = graph
                )
        }
}

//@Embeddable
//data class UserInfo(
//        @Column(name = "nickname", unique = true, length = 20)
//        var nickname: String
//)