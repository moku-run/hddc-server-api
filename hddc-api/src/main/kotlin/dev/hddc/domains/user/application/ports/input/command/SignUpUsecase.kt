package dev.hddc.domains.user.application.ports.input.command

data class SignUpCommand(
    val email: String,
    val password: String,
    val nickname: String,
)

interface SignUpUsecase {
    fun execute(command: SignUpCommand): Long
}
