package dev.hddc.domains.admin.application.ports.input.command

data class AdminLoginCommand(
    val email: String,
    val password: String,
)

data class AdminLoginResult(
    val token: String,
    val name: String,
    val role: String,
)

interface AdminLoginUsecase {
    fun execute(command: AdminLoginCommand): AdminLoginResult
}
