package dev.hddc.domains.user.application.ports.input.command

data class PasswordResetCommand(
    val email: String,
    val password: String,
    val passwordConfirm: String,
)

interface PasswordResetUsecase {
    fun sendCode(email: String)
    fun verifyCode(email: String, code: String)
    fun reset(command: PasswordResetCommand)
}
