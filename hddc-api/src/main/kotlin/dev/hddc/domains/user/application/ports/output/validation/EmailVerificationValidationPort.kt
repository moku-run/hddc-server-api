package dev.hddc.domains.user.application.ports.output.validation

interface EmailVerificationValidationPort {
    fun requireEmailVerified(email: String)
}
