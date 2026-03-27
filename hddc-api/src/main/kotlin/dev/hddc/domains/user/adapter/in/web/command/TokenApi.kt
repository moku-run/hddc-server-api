package dev.hddc.domains.user.adapter.`in`.web.command

import dev.hddc.domains.user.application.ports.input.command.LogoutUsecase
import dev.hddc.domains.user.application.ports.input.command.TokenRefreshUsecase
import dev.hddc.framework.api.response.ApiResponse
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.api.response.ApiResult
import dev.hddc.framework.security.jwt.spec.JwtSpec
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Token", description = "토큰 관리 API")
@RestController
class TokenApi(
    private val tokenRefreshUsecase: TokenRefreshUsecase,
    private val logoutUsecase: LogoutUsecase,
    private val refreshCookieHelper: RefreshCookieHelper,
) {
    @Operation(summary = "토큰 갱신")
    @PostMapping("/api/auth/refresh")
    fun refresh(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ApiResult<Map<String, String>> {
        val refreshToken = request.cookies
            ?.find { it.name == JwtSpec.REFRESH_COOKIE_NAME }?.value
            ?: throw IllegalArgumentException(ApiResponseCode.TOKEN_INVALID.code)

        val tokenPair = tokenRefreshUsecase.refresh(refreshToken)
        refreshCookieHelper.addRefreshCookie(response, tokenPair.refreshToken)

        return ResponseEntity
            .status(ApiResponseCode.OK.status)
            .header(HttpHeaders.AUTHORIZATION, "${JwtSpec.TOKEN_PREFIX}${tokenPair.accessToken}")
            .body(
                ApiResponse(
                    success = true,
                    code = ApiResponseCode.OK.code,
                    message = ApiResponseCode.OK.message,
                    payload = mapOf("token" to tokenPair.accessToken),
                )
            )
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/api/auth/logout")
    fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ApiResult<Nothing> {
        val token = request.getHeader(JwtSpec.TOKEN_HEADER)
            ?.removePrefix(JwtSpec.TOKEN_PREFIX)
        logoutUsecase.execute(token)
        refreshCookieHelper.clearRefreshCookie(response)
        return ApiResponse.of(ApiResponseCode.OK)
    }
}
