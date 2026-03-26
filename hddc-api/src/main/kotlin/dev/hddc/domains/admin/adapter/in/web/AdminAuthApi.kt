package dev.hddc.domains.admin.adapter.`in`.web

import dev.hddc.domains.admin.application.ports.input.command.AdminLoginCommand
import dev.hddc.domains.admin.application.ports.input.command.AdminLoginResult
import dev.hddc.domains.admin.application.ports.input.command.AdminLoginUsecase
import dev.hddc.framework.api.response.ApiResponse
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.api.response.ApiResult
import dev.hddc.framework.security.jwt.spec.JwtSpec
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

data class AdminLoginRequest(
    @field:NotBlank(message = "이메일은 필수입니다.")
    val email: String,
    @field:NotBlank(message = "비밀번호는 필수입니다.")
    val password: String,
)

@Tag(name = "Admin Auth", description = "관리자 인증 API")
@RestController
class AdminAuthApi(
    private val adminLoginUsecase: AdminLoginUsecase,
) {
    @Operation(summary = "관리자 로그인")
    @PostMapping("/api/admin/auth/login")
    fun login(
        @Valid @RequestBody request: AdminLoginRequest,
    ): ApiResult<AdminLoginResult> {
        val result = adminLoginUsecase.execute(AdminLoginCommand(request.email, request.password))
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
}
