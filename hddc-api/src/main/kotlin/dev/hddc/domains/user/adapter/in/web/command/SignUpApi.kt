package dev.hddc.domains.user.adapter.`in`.web.command

import dev.hddc.domains.user.adapter.`in`.web.request.EmailVerificationSendRequest
import dev.hddc.domains.user.adapter.`in`.web.request.EmailVerificationVerifyRequest
import dev.hddc.domains.user.adapter.`in`.web.request.SignUpRequest
import dev.hddc.domains.user.application.ports.input.command.EmailVerificationUsecase
import dev.hddc.domains.user.application.ports.input.command.SignUpUsecase
import dev.hddc.domains.user.application.ports.input.query.CheckNicknameResult
import dev.hddc.domains.user.application.ports.input.query.CheckNicknameUsecase
import dev.hddc.framework.api.response.ApiResponse
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.api.response.ApiResult
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Sign Up", description = "회원가입 API")
@RestController
class SignUpApi(
    private val emailVerificationUsecase: EmailVerificationUsecase,
    private val signUpUsecase: SignUpUsecase,
    private val checkNicknameUsecase: CheckNicknameUsecase,
) {
    @Operation(summary = "이메일 인증 코드 발송")
    @PostMapping("/api/auth/email-verifications")
    fun sendVerification(
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

    @Operation(summary = "회원가입")
    @PostMapping("/api/auth/sign-up")
    fun signUp(
        @Valid @RequestBody request: SignUpRequest,
    ): ApiResult<Long> =
        ApiResponse.of(ApiResponseCode.CREATED, signUpUsecase.execute(request.toCommand()))

    @Operation(summary = "닉네임 중복 확인")
    @GetMapping("/api/auth/check-nickname")
    fun checkNickname(
        @RequestParam nickname: String,
    ): ApiResult<CheckNicknameResult> =
        ApiResponse.of(ApiResponseCode.OK, checkNicknameUsecase.execute(nickname))
}
