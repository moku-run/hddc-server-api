package dev.hddc.domains.user.adapter.out.security

import dev.hddc.domains.user.application.ports.output.security.PasswordEncodePort
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class PasswordEncodeAdapter(
    private val passwordEncoder: PasswordEncoder,
) : PasswordEncodePort {

    override fun encode(rawPassword: String): String =
        passwordEncoder.encode(rawPassword)

    override fun matches(rawPassword: String, encodedPassword: String): Boolean =
        passwordEncoder.matches(rawPassword, encodedPassword)
}
