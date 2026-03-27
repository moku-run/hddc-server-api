package dev.hddc.domains.user.application.ports.output.security

interface TokenPort {
    fun createAccessToken(email: String, role: String): String
}
