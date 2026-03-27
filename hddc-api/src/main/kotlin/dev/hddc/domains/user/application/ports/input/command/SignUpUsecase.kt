package dev.hddc.domains.user.application.ports.input.command

data class SignUpCommand(
    val email: String,
    val password: String,
    val nickname: String,
)

data class SignUpResult(
    val userId: Long,
)

interface SignUpUsecase {
    fun execute(command: SignUpCommand): SignUpResult
}
