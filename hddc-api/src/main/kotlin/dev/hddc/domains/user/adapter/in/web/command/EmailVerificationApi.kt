package dev.hddc.domains.user.adapter.`in`.web.command

import dev.hddc.domains.user.adapter.`in`.web.request.EmailVerificationSendRequest
import dev.hddc.domains.user.adapter.`in`.web.request.EmailVerificationVerifyRequest
import dev.hddc.domains.user.application.ports.input.command.EmailVerificationUsecase
import dev.hddc.framework.api.response.ApiResponse
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.api.response.ApiResult
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Email Verification", description = "이메일 인증 API")
@RestController
class EmailVerificationApi(
    private val emailVerificationUsecase: EmailVerificationUsecase,
) {
    @Operation(summary = "이메일 인증 코드 발송")
    @PostMapping("/api/auth/email-verifications")
    fun send(
        @Valid @RequestBody request: EmailVerificationSendRequest,
    ): ApiResult<Nothing> {
        emailVerificationUsecase.send(request.email)
        return ApiResponse.of(ApiResponseCode.VERIFICATION_CODE_SENT)
    }

    @Operation(summary = "이메일 인증 코드 확인")
    @PostMapping("/api/auth/email-verifications/verify")
    fun verify(
        @Valid @RequestBody request: EmailVerificationVerifyRequest,
    ): ApiResult<Nothing> {
        emailVerificationUsecase.verify(request.email, request.code)
        return ApiResponse.of(ApiResponseCode.VERIFICATION_COMPLETED)
    }
}
