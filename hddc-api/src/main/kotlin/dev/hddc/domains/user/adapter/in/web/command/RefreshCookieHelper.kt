package dev.hddc.domains.user.adapter.`in`.web.command

import dev.hddc.framework.security.jwt.spec.JwtSpec
import dev.hddc.framework.security.jwt.value.JwtProperties
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component

@Component
class RefreshCookieHelper(
    private val jwtProperties: JwtProperties,
) {
    fun addRefreshCookie(response: HttpServletResponse, token: String) {
        val cookie = Cookie(JwtSpec.REFRESH_COOKIE_NAME, token).apply {
            isHttpOnly = true
            secure = jwtProperties.cookieSecure
            path = "/api/auth"
            maxAge = (jwtProperties.refreshExpiredMs / 1000).toInt()
        }
        response.addCookie(cookie)
    }

    fun clearRefreshCookie(response: HttpServletResponse) {
        val cookie = Cookie(JwtSpec.REFRESH_COOKIE_NAME, "").apply {
            isHttpOnly = true
            secure = jwtProperties.cookieSecure
            path = "/api/auth"
            maxAge = 0
        }
        response.addCookie(cookie)
    }
}
