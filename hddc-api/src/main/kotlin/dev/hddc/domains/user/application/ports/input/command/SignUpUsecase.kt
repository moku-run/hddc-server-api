package dev.hddc.domains.user.application.ports.input.command

import dev.hddc.domains.user.domain.model.CreateUserModel

data class SignUpCommand(
    val email: String,
    val password: String,
    val nickname: String,
) {
    fun toCreateModel(encodedPassword: String) = CreateUserModel(
        email = email,
        password = encodedPassword,
        nickname = nickname,
    )
}

data class SignUpResult(
    val userId: Long,
)

interface SignUpUsecase {
    fun execute(command: SignUpCommand): SignUpResult
}
