package dev.hddc.domains.user.application.ports.output.security

data class TokenPair(
    val accessToken: String,
    val refreshToken: String,
)

interface TokenPort {
    fun createTokenPair(email: String, role: String): TokenPair
    fun refreshTokenPair(refreshToken: String): TokenPair
    fun revokeTokens(accessToken: String?)
}
