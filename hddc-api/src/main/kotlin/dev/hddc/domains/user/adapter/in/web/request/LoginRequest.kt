package dev.hddc.domains.user.adapter.`in`.web.request

import dev.hddc.domains.user.application.ports.input.command.LoginCommand
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank(message = "이메일은 필수입니다.")
    @field:Email(message = "올바른 이메일 형식이 아닙니다.")
    val email: String,

    @field:NotBlank(message = "비밀번호는 필수입니다.")
    val password: String,
) {
    fun toCommand(): LoginCommand = LoginCommand(
        email = email,
        password = password,
    )
}
