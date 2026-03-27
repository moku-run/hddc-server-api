package dev.hddc.domains.user.application.ports.input.command

import dev.hddc.domains.user.application.ports.output.security.TokenPair

interface TokenRefreshUsecase {
    fun refresh(refreshToken: String): TokenPair
}
