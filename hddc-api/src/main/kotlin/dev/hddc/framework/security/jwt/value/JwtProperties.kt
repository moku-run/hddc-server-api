package dev.hddc.framework.security.jwt.value

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val issuer: String,
    val secretKey: String,
    val expiredMs: Long,
    val refreshExpiredMs: Long = 604800000,
    val cookieSecure: Boolean = false,
)
