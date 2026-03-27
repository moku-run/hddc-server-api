package dev.hddc.domains.user.adapter.`in`.web.command

import dev.hddc.domains.user.adapter.`in`.web.request.LoginRequest
import dev.hddc.domains.user.application.ports.input.command.LoginResult
import dev.hddc.domains.user.application.ports.input.command.LoginUsecase
import dev.hddc.framework.api.response.ApiResponse
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.api.response.ApiResult
import dev.hddc.framework.security.jwt.spec.JwtSpec
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Login", description = "로그인 API")
@RestController
class LoginApi(
    private val loginUsecase: LoginUsecase,
    private val refreshCookieHelper: RefreshCookieHelper,
) {
    @Operation(summary = "로그인")
    @PostMapping("/api/auth/login")
    fun login(
        @Valid @RequestBody request: LoginRequest,
        response: HttpServletResponse,
    ): ApiResult<LoginResult> {
        val result = loginUsecase.execute(request.toCommand())
        refreshCookieHelper.addRefreshCookie(response, result.refreshToken)

        return ResponseEntity
            .status(ApiResponseCode.OK.status)
            .header(HttpHeaders.AUTHORIZATION, "${JwtSpec.TOKEN_PREFIX}${result.accessToken}")
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
