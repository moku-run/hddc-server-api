package dev.hddc.framework.security.jwt

import org.springframework.stereotype.Service

@Service
class JwtService(
    private val jwtCreator: JwtCreator,
    private val jwtParser: JwtParser,
    private val jwtValidator: JwtValidator,
    private val jwtRemover: JwtRemover,
) {
    fun create(username: String, role: String): String =
        jwtCreator.create(username, role)

    fun getUsername(token: String): String =
        jwtParser.getUsername(token)

    fun getRole(token: String): String =
        jwtParser.getRole(token)

    fun isValid(token: String): Boolean =
        jwtValidator.isValid(token)

    fun remove(token: String) =
        jwtRemover.remove(token)
}
