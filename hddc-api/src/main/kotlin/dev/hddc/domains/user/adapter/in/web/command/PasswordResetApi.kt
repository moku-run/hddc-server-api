package dev.hddc.domains.user.adapter.`in`.web.command

import dev.hddc.domains.user.adapter.`in`.web.request.PasswordResetRequest
import dev.hddc.domains.user.adapter.`in`.web.request.PasswordResetSendRequest
import dev.hddc.domains.user.adapter.`in`.web.request.PasswordResetVerifyRequest
import dev.hddc.domains.user.application.ports.input.command.PasswordResetUsecase
import dev.hddc.framework.api.response.ApiResponse
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.api.response.ApiResult
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Password Reset", description = "비밀번호 찾기/재설정 API")
@RestController
class PasswordResetApi(
    private val passwordResetUsecase: PasswordResetUsecase,
) {
    @Operation(summary = "비밀번호 찾기 - 인증 코드 발송")
    @PostMapping("/api/auth/password-reset/email-verifications")
    fun sendPasswordResetCode(
        @Valid @RequestBody request: PasswordResetSendRequest,
    ): ApiResult<Nothing> {
        passwordResetUsecase.sendCode(request.email)
        return ApiResponse.of(ApiResponseCode.VERIFICATION_CODE_SENT)
    }

    @Operation(summary = "비밀번호 찾기 - 인증 코드 확인")
    @PostMapping("/api/auth/password-reset/email-verifications/verify")
    fun verifyPasswordResetCode(
        @Valid @RequestBody request: PasswordResetVerifyRequest,
    ): ApiResult<Nothing> {
        passwordResetUsecase.verifyCode(request.email, request.code)
        return ApiResponse.of(ApiResponseCode.VERIFICATION_COMPLETED)
    }

    @Operation(summary = "비밀번호 재설정")
    @PutMapping("/api/auth/password-reset")
    fun resetPassword(
        @Valid @RequestBody request: PasswordResetRequest,
    ): ApiResult<Nothing> {
        passwordResetUsecase.reset(request.toCommand())
        return ApiResponse.of(ApiResponseCode.UPDATED)
    }
}
