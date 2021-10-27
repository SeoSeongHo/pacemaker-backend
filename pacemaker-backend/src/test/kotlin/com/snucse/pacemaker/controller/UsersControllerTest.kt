package com.snucse.pacemaker.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.snucse.pacemaker.dto.UserDto
import com.snucse.pacemaker.exception.UserNotFoundException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
internal class UsersControllerTest(
        @Autowired private val mvc: MockMvc
) {


    @Test
    @Transactional
    fun signUpAndsignIn() {
        val signUpReq = UserDto.SignUpReq("test@test.com", "1234","test1")
        val signUpjsonData = jacksonObjectMapper().writeValueAsString(signUpReq)

        mvc.perform(
                post("/api/v1/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signUpjsonData))
                .andExpect(status().isOk)

        val signInReq = UserDto.SignInReq("test@test.com", "1234")
        val signInjsonData = jacksonObjectMapper().writeValueAsString(signInReq)

        mvc.perform(
                post("/api/v1/users/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signInjsonData))
                .andExpect(status().isOk)

    }



    @Test
    fun test() {
        mvc.perform(
                get("/api/v1/users/test", null)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().string("sucess"))
    }
}