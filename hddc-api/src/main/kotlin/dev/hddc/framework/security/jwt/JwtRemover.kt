package dev.hddc.framework.security.jwt

import dev.hddc.framework.security.jwt.spec.JwtSpec
import dev.hddc.framework.security.jwt.value.JwtProperties
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class JwtRemover(
    private val jwtProperties: JwtProperties,
    private val redisTemplate: StringRedisTemplate,
) {
    fun remove(token: String) {
        val key = "${JwtSpec.BLACK_LIST_KEY}$token"
        redisTemplate.opsForValue().set(key, "blacklisted", jwtProperties.expiredMs, TimeUnit.MILLISECONDS)
    }
}
