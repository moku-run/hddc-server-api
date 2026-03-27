package dev.hddc.domains.user.adapter.out.security

import dev.hddc.domains.user.application.ports.output.security.TokenPair
import dev.hddc.domains.user.application.ports.output.security.TokenPort
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.security.jwt.JwtService
import org.springframework.stereotype.Component

@Component
class TokenAdapter(
    private val jwtService: JwtService,
) : TokenPort {

    override fun createTokenPair(email: String, role: String): TokenPair =
        TokenPair(
            accessToken = jwtService.create(email, role),
            refreshToken = jwtService.createRefresh(email, role),
        )

    override fun refreshTokenPair(refreshToken: String): TokenPair {
        require(jwtService.isRefreshTokenValid(refreshToken)) {
            ApiResponseCode.TOKEN_INVALID.code
        }
        val username = jwtService.getUsername(refreshToken)
        val role = jwtService.getRole(refreshToken)
        return createTokenPair(username, role)
    }

    override fun revokeTokens(accessToken: String?) {
        if (accessToken != null) {
            jwtService.remove(accessToken)
            val username = try { jwtService.getUsername(accessToken) } catch (_: Exception) { null }
            if (username != null) {
                jwtService.removeRefreshToken(username)
            }
        }
    }
}
