package dev.hddc.domains.user.application.ports.output.command

interface EmailSendPort {
    fun sendVerificationCode(toEmail: String, code: String)
}
