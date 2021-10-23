package com.snucse.pacemaker.controller

import com.snucse.pacemaker.dto.UserDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.snucse.pacemaker.service.users.UserService
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/users")
class UsersController (
        @Autowired private val userService: UserService
) {

    @PostMapping("/signup")
    fun signUp(@RequestBody @Valid signUpReq: UserDto.SignUpReq): ResponseEntity<UserDto.SignUpRes> {

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body(userService.signUp(signUpReq))
    }

    @PostMapping("/signin")
    fun singIn(@RequestBody signInReq: UserDto.SignInReq): ResponseEntity<UserDto.SignInRes> {

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body(userService.signIn(signInReq))
    }

    @PostMapping("/signout")
    fun signOut() {
        TODO()
    }

    @PostMapping("/updatenickname")
    fun updateNickname() {
        TODO()
    }
}