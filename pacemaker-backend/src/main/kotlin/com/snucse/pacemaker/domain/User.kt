package com.snucse.pacemaker.domain

import javax.persistence.*

@Entity
@Table(name = "users")
data class User (
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long,
        @Column(name = "email", unique = true, length = 200)
        var email: String,
        var password: String,
        @Embedded
        var userInfo: UserInfo
)

@Embeddable
data class UserInfo(
        @Column(name = "nickname", unique = true, length = 20)
        var nickname: String
)