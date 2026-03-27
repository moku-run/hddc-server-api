package dev.hddc.domains.user.application.ports.output.validation

interface PasswordResetVerificationValidator {
    fun requireVerified(email: String)
}
