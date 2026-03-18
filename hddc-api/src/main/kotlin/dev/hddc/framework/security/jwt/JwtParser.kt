package dev.hddc.framework.security.jwt

import dev.hddc.framework.security.jwt.spec.JwtSpec
import dev.hddc.framework.security.jwt.value.JwtProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component

@Component
class JwtParser(
    private val jwtProperties: JwtProperties,
) {
    fun removePrefix(token: String): String =
        token.removePrefix(JwtSpec.TOKEN_PREFIX)

    fun getClaims(token: String): Claims {
        val key = Keys.hmacShaKeyFor(jwtProperties.secretKey.toByteArray())
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(removePrefix(token))
            .payload
    }

    fun getUsername(token: String): String =
        getClaims(token).get(JwtSpec.CLAIM_USERNAME, String::class.java)

    fun getRole(token: String): String =
        getClaims(token).get(JwtSpec.CLAIM_ROLE, String::class.java)
}
