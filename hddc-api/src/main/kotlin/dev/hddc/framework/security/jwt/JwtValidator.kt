package dev.hddc.framework.security.jwt

import dev.hddc.framework.security.jwt.spec.JwtSpec
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class JwtValidator(
    private val jwtParser: JwtParser,
    private val redisTemplate: StringRedisTemplate,
) {
    fun isValid(token: String): Boolean {
        return try {
            if (isBlacklisted(token)) return false

            val claims = jwtParser.getClaims(token)
            val username = claims.get(JwtSpec.CLAIM_USERNAME, String::class.java)

            !username.isNullOrBlank()
        } catch (e: Exception) {
            false
        }
    }

    private fun isBlacklisted(token: String): Boolean =
        redisTemplate.hasKey("${JwtSpec.BLACK_LIST_KEY}$token")
}
