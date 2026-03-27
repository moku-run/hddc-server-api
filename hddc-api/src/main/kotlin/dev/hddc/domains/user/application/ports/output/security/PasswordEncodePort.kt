package dev.hddc.domains.user.application.ports.output.security

interface PasswordEncodePort {
    fun encode(rawPassword: String): String
    fun matches(rawPassword: String, encodedPassword: String): Boolean
}
