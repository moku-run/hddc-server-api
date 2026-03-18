package dev.hddc.domains.user.application.ports.input.command

interface EmailVerificationUsecase {
    fun send(email: String)
    fun verify(email: String, code: String)
}
