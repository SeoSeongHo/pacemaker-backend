package com.snucse.pacemaker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PacemakerApplication

fun main(args: Array<String>) {
	runApplication<PacemakerApplication>(*args)
}
