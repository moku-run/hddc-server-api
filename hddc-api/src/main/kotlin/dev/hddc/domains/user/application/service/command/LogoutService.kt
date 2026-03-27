package dev.hddc.domains.user.application.service.command

import dev.hddc.domains.user.application.ports.input.command.LogoutUsecase
import dev.hddc.domains.user.application.ports.output.security.TokenPort
import org.springframework.stereotype.Service

@Service
class LogoutService(
    private val tokenPort: TokenPort,
) : LogoutUsecase {

    override fun execute(accessToken: String?) {
        tokenPort.revokeTokens(accessToken)
    }
}
