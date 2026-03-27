package dev.hddc.domains.user.application.ports.input.command

data class LoginCommand(
    val email: String,
    val password: String,
)

data class LoginResult(
    val userId: Long,
    val email: String,
    val nickname: String,
    val role: String,
    val accessToken: String,
    val refreshToken: String,
)

interface LoginUsecase {
    fun execute(command: LoginCommand): LoginResult
}
