package dev.hddc.domains.user.adapter.`in`.web.command

import dev.hddc.domains.user.adapter.`in`.web.request.EmailVerificationSendRequest
import dev.hddc.domains.user.adapter.`in`.web.request.EmailVerificationVerifyRequest
import dev.hddc.domains.user.adapter.`in`.web.request.LoginRequest
import dev.hddc.domains.user.adapter.`in`.web.request.PasswordResetRequest
import dev.hddc.domains.user.adapter.`in`.web.request.PasswordResetSendRequest
import dev.hddc.domains.user.adapter.`in`.web.request.PasswordResetVerifyRequest
import dev.hddc.domains.user.adapter.`in`.web.request.SignUpRequest
import dev.hddc.domains.user.application.ports.input.command.EmailVerificationUsecase
import dev.hddc.domains.user.application.ports.input.command.LoginResult
import dev.hddc.domains.user.application.ports.input.command.LoginUsecase
import dev.hddc.domains.user.application.ports.input.command.PasswordResetUsecase
import dev.hddc.domains.user.application.ports.input.command.SignUpUsecase
import dev.hddc.domains.user.application.ports.input.query.CheckNicknameResult
import dev.hddc.domains.user.application.ports.input.query.CheckNicknameUsecase
import dev.hddc.framework.api.response.ApiResponse
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.security.jwt.JwtService
import dev.hddc.framework.security.jwt.spec.JwtSpec
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Auth", description = "인증 API")
@RestController
class AuthApi(
    private val emailVerificationUsecase: EmailVerificationUsecase,
    private val signUpUsecase: SignUpUsecase,
    private val loginUsecase: LoginUsecase,
    private val passwordResetUsecase: PasswordResetUsecase,
    private val checkNicknameUsecase: CheckNicknameUsecase,
    private val jwtService: JwtService,
) {
    @Operation(summary = "회원가입 - 이메일 인증 코드 발송")
    @PostMapping("/api/auth/email-verifications")
    fun sendVerification(
        @Valid @RequestBody request: EmailVerificationSendRequest,
    ): ResponseEntity<ApiResponse<Nothing>> {
        emailVerificationUsecase.send(request.email)
        return ApiResponse.of(ApiResponseCode.VERIFICATION_CODE_SENT)
    }

    @Operation(summary = "회원가입 - 이메일 인증 코드 확인")
    @PostMapping("/api/auth/email-verifications/verify")
    fun verify(
        @Valid @RequestBody request: EmailVerificationVerifyRequest,
    ): ResponseEntity<ApiResponse<Nothing>> {
        emailVerificationUsecase.verify(request.email, request.code)
        return ApiResponse.of(ApiResponseCode.VERIFICATION_COMPLETED)
    }

    @Operation(summary = "회원가입")
    @PostMapping("/api/auth/sign-up")
    fun signUp(
        @Valid @RequestBody request: SignUpRequest,
    ): ResponseEntity<ApiResponse<Long>> =
        ApiResponse.of(ApiResponseCode.CREATED, signUpUsecase.execute(request.toCommand()))

    @Operation(summary = "로그인")
    @PostMapping("/api/auth/login")
    fun login(
        @Valid @RequestBody request: LoginRequest,
    ): ResponseEntity<ApiResponse<LoginResult>> {
        val result = loginUsecase.execute(request.toCommand())
        return ResponseEntity
            .status(ApiResponseCode.OK.status)
            .header(HttpHeaders.AUTHORIZATION, "${JwtSpec.TOKEN_PREFIX}${result.token}")
            .body(
                ApiResponse(
                    success = true,
                    code = ApiResponseCode.OK.code,
                    message = ApiResponseCode.OK.message,
                    payload = result,
                )
            )
    }

    @Operation(summary = "비밀번호 찾기 - 인증 코드 발송")
    @PostMapping("/api/auth/password-reset/email-verifications")
    fun sendPasswordResetCode(
        @Valid @RequestBody request: PasswordResetSendRequest,
    ): ResponseEntity<ApiResponse<Nothing>> {
        passwordResetUsecase.sendCode(request.email)
        return ApiResponse.of(ApiResponseCode.VERIFICATION_CODE_SENT)
    }

    @Operation(summary = "비밀번호 찾기 - 인증 코드 확인")
    @PostMapping("/api/auth/password-reset/email-verifications/verify")
    fun verifyPasswordResetCode(
        @Valid @RequestBody request: PasswordResetVerifyRequest,
    ): ResponseEntity<ApiResponse<Nothing>> {
        passwordResetUsecase.verifyCode(request.email, request.code)
        return ApiResponse.of(ApiResponseCode.VERIFICATION_COMPLETED)
    }

    @Operation(summary = "비밀번호 재설정")
    @PutMapping("/api/auth/password-reset")
    fun resetPassword(
        @Valid @RequestBody request: PasswordResetRequest,
    ): ResponseEntity<ApiResponse<Nothing>> {
        passwordResetUsecase.reset(request.toCommand())
        return ApiResponse.of(ApiResponseCode.UPDATED)
    }

    @Operation(summary = "닉네임 중복 확인")
    @GetMapping("/api/auth/check-nickname")
    fun checkNickname(
        @RequestParam nickname: String,
    ): ResponseEntity<ApiResponse<CheckNicknameResult>> =
        ApiResponse.of(ApiResponseCode.OK, checkNicknameUsecase.execute(nickname))

    @Operation(summary = "로그아웃")
    @PostMapping("/api/auth/logout")
    fun logout(
        request: HttpServletRequest,
    ): ResponseEntity<ApiResponse<Nothing>> {
        val token = request.getHeader(JwtSpec.TOKEN_HEADER)
            ?.removePrefix(JwtSpec.TOKEN_PREFIX)
        if (token != null) {
            jwtService.remove(token)
        }
        return ApiResponse.of(ApiResponseCode.OK)
    }
}
