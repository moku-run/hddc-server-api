package dev.hddc.framework.security.jwt

import dev.hddc.framework.security.jwt.spec.JwtSpec
import dev.hddc.framework.security.jwt.value.JwtProperties
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtCreator(
    private val jwtProperties: JwtProperties,
) {
    fun create(username: String, role: String): String {
        val now = Date()
        val expiration = Date(now.time + jwtProperties.expiredMs)
        val key = Keys.hmacShaKeyFor(jwtProperties.secretKey.toByteArray())

        return Jwts.builder()
            .issuer(jwtProperties.issuer)
            .issuedAt(now)
            .expiration(expiration)
            .claim(JwtSpec.CLAIM_USERNAME, username)
            .claim(JwtSpec.CLAIM_ROLE, role)
            .signWith(key)
            .compact()
    }
}
