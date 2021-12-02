package com.snucse.pacemaker.controller

import com.snucse.pacemaker.dto.AuthPrincipal
import com.snucse.pacemaker.dto.MatchDto
import com.snucse.pacemaker.service.match.MatchService
import com.snucse.pacemaker.service.match.consumer.MatchQueueConsumer
import com.snucse.pacemaker.service.match.queue.RedisMatchQueue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/matches")
class MatchController(
        @Autowired private val matchService: MatchService,
        @Autowired private val matchQueueConsumer: MatchQueueConsumer
) {

    @PostMapping
    fun match(@AuthenticationPrincipal authPrincipal: AuthPrincipal, @RequestBody matchReq: MatchDto.MatchReq)
    : ResponseEntity<MatchDto.MatchRes> {

        // TODO 따로 컨슘 메서드 구현
        matchQueueConsumer.consume()

        val matchRes = matchService.match(matchReq, authPrincipal.userId)

        return ResponseEntity
                    .ok()
                    .body(matchRes)
    }

    @PostMapping("/inMatchPolling")
    fun inMatchPolling(@RequestBody inMatchReq: MatchDto.InMatchReq): ResponseEntity<MatchDto.InMatchRes> {
        val inMatchRes = matchService.inMatchPolling(inMatchReq)

        return ResponseEntity
                .ok()
                .body(inMatchRes)
    }

    @GetMapping
    fun test(){

        val keys = RedisMatchQueue.matchQueues.keys
        println(RedisMatchQueue.matchQueues.keys)

        for (key in keys) {
            println("$key : ${RedisMatchQueue.matchQueues[key]}")
        }
    }
}