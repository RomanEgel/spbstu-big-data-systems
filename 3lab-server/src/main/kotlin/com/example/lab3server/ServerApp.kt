package com.example.lab3server

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class ServerApp

fun main(args: Array<String>) {
    runApplication<ServerApp>(*args)
}

@RestController
@RequestMapping("/server")
class ServerController {
    val logger: Logger = LoggerFactory.getLogger(ServerController::class.java)

    @GetMapping("/data")
    fun getData(): String {
        logger.info("Returning data from server")
        return "Hello from server"
    }

}