package dev.hddc

import dev.hddc.framework.security.jwt.value.JwtProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties::class)
class HddcApplication

fun main(args: Array<String>) {
    runApplication<HddcApplication>(*args)
}
