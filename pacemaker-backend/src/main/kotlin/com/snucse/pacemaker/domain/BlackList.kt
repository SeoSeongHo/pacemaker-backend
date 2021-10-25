package com.snucse.pacemaker.domain


import javax.persistence.*;

@Entity
data class BlackList(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,
        @Column(unique = true)
        val token: String
) {

}