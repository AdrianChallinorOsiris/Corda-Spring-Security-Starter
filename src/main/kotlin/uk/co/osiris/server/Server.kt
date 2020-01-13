package uk.co.osiris.server

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication


@SpringBootApplication
private open class Tradecord

fun main(args: Array<String>) {
    val app = SpringApplication(Tradecord::class.java)
    app.run(*args)
}

