package dev.hddc.framework.security.jwt

import dev.hddc.framework.security.jwt.spec.JwtSpec
import dev.hddc.framework.security.jwt.value.JwtProperties
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class JwtService(
    private val jwtCreator: JwtCreator,
    private val jwtParser: JwtParser,
    private val jwtValidator: JwtValidator,
    private val jwtRemover: JwtRemover,
    private val redisTemplate: StringRedisTemplate,
    private val jwtProperties: JwtProperties,
) {
    fun create(username: String, role: String): String =
        jwtCreator.create(username, role)

    fun createRefresh(username: String, role: String): String {
        val refreshToken = jwtCreator.createRefresh(username, role)
        val key = "${JwtSpec.REFRESH_KEY}$username"
        redisTemplate.opsForValue().set(key, refreshToken, jwtProperties.refreshExpiredMs, TimeUnit.MILLISECONDS)
        return refreshToken
    }

    fun getUsername(token: String): String =
        jwtParser.getUsername(token)

    fun getRole(token: String): String =
        jwtParser.getRole(token)

    fun getTokenType(token: String): String =
        jwtParser.getTokenType(token)

    fun isValid(token: String): Boolean =
        jwtValidator.isValid(token)

    fun isRefreshTokenValid(token: String): Boolean {
        if (!jwtValidator.isValid(token)) return false
        val type = jwtParser.getTokenType(token)
        if (type != JwtSpec.TOKEN_TYPE_REFRESH) return false
        val username = jwtParser.getUsername(token)
        val stored = redisTemplate.opsForValue().get("${JwtSpec.REFRESH_KEY}$username")
        return stored == token
    }

    fun removeRefreshToken(username: String) {
        redisTemplate.delete("${JwtSpec.REFRESH_KEY}$username")
    }

    fun remove(token: String) =
        jwtRemover.remove(token)
}
