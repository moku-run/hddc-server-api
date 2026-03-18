package dev.hddc.domains.user.adapter.`in`.web.request

import dev.hddc.domains.user.application.ports.input.command.PasswordResetCommand
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class PasswordResetSendRequest(
    @field:NotBlank(message = "이메일은 필수입니다.")
    @field:Email(message = "올바른 이메일 형식이 아닙니다.")
    val email: String,
)

data class PasswordResetVerifyRequest(
    @field:NotBlank(message = "이메일은 필수입니다.")
    @field:Email(message = "올바른 이메일 형식이 아닙니다.")
    val email: String,

    @field:NotBlank(message = "인증 코드는 필수입니다.")
    val code: String,
)

data class PasswordResetRequest(
    @field:NotBlank(message = "이메일은 필수입니다.")
    @field:Email(message = "올바른 이메일 형식이 아닙니다.")
    val email: String,

    @field:NotBlank(message = "비밀번호는 필수입니다.")
    @field:Size(min = 8, max = 20, message = "비밀번호는 8~20자여야 합니다.")
    val password: String,

    @field:NotBlank(message = "비밀번호 확인은 필수입니다.")
    val passwordConfirm: String,
) {
    fun toCommand(): PasswordResetCommand = PasswordResetCommand(
        email = email,
        password = password,
        passwordConfirm = passwordConfirm,
    )
}
