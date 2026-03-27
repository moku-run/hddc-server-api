package dev.hddc.domains.user.adapter.out.security

import dev.hddc.domains.user.application.ports.output.security.TokenPort
import dev.hddc.framework.security.jwt.JwtService
import org.springframework.stereotype.Component

@Component
class TokenAdapter(
    private val jwtService: JwtService,
) : TokenPort {

    override fun createAccessToken(email: String, role: String): String =
        jwtService.create(email, role)
}
