package dev.hddc.domains.user.adapter.`in`.web.command

import dev.hddc.framework.api.response.ApiResponse
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.api.response.ApiResult
import dev.hddc.framework.security.jwt.JwtService
import dev.hddc.framework.security.jwt.spec.JwtSpec
import dev.hddc.framework.security.jwt.value.JwtProperties
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Token", description = "토큰 관리 API")
@RestController
class TokenApi(
    private val jwtService: JwtService,
    private val jwtProperties: JwtProperties,
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

        require(jwtService.isRefreshTokenValid(refreshToken)) {
            ApiResponseCode.TOKEN_INVALID.code
        }

        val username = jwtService.getUsername(refreshToken)
        val role = jwtService.getRole(refreshToken)

        val newAccessToken = jwtService.create(username, role)
        val newRefreshToken = jwtService.createRefresh(username, role)
        addRefreshCookie(response, newRefreshToken)

        return ResponseEntity
            .status(ApiResponseCode.OK.status)
            .header(HttpHeaders.AUTHORIZATION, "${JwtSpec.TOKEN_PREFIX}$newAccessToken")
            .body(
                ApiResponse(
                    success = true,
                    code = ApiResponseCode.OK.code,
                    message = ApiResponseCode.OK.message,
                    payload = mapOf("token" to newAccessToken),
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
        if (token != null) {
            jwtService.remove(token)
            val username = try { jwtService.getUsername(token) } catch (_: Exception) { null }
            if (username != null) {
                jwtService.removeRefreshToken(username)
            }
        }
        clearRefreshCookie(response)
        return ApiResponse.of(ApiResponseCode.OK)
    }

    private fun addRefreshCookie(response: HttpServletResponse, token: String) {
        val cookie = Cookie(JwtSpec.REFRESH_COOKIE_NAME, token).apply {
            isHttpOnly = true
            secure = jwtProperties.cookieSecure
            path = "/api/auth"
            maxAge = (jwtProperties.refreshExpiredMs / 1000).toInt()
        }
        response.addCookie(cookie)
    }

    private fun clearRefreshCookie(response: HttpServletResponse) {
        val cookie = Cookie(JwtSpec.REFRESH_COOKIE_NAME, "").apply {
            isHttpOnly = true
            secure = jwtProperties.cookieSecure
            path = "/api/auth"
            maxAge = 0
        }
        response.addCookie(cookie)
    }
}
