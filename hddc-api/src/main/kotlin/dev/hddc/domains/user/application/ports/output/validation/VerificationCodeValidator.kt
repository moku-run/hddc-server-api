package dev.hddc.domains.user.application.ports.output.validation

interface VerificationCodeValidator {
    fun validateCode(cacheKey: String, attemptsKey: String, inputCode: String)
}
