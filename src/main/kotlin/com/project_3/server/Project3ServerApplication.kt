package com.project_3.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class Project3ServerApplication

fun main(args: Array<String>) {
    runApplication<Project3ServerApplication>(*args)
}
