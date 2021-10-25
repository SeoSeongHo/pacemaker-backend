package com.snucse.pacemaker.dto

class OAuthDto {

    data class OAuthReq(
            val token: String
    )


    data class OAuthRes(
            val token: String
    )
}