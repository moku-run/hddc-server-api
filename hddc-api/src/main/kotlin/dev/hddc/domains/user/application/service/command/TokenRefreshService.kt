package dev.hddc.domains.user.application.service.command

import dev.hddc.domains.user.application.ports.input.command.TokenRefreshUsecase
import dev.hddc.domains.user.application.ports.output.security.TokenPair
import dev.hddc.domains.user.application.ports.output.security.TokenPort
import org.springframework.stereotype.Service

@Service
class TokenRefreshService(
    private val tokenPort: TokenPort,
) : TokenRefreshUsecase {

    override fun refresh(refreshToken: String): TokenPair =
        tokenPort.refreshTokenPair(refreshToken)
}
