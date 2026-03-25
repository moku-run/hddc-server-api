package dev.hddc

import dev.hddc.framework.security.jwt.value.JwtProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties::class)
@EnableAsync
@EnableScheduling
class HddcApplication

fun main(args: Array<String>) {
    runApplication<HddcApplication>(*args)
}
