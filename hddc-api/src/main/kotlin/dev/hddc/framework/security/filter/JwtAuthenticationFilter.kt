package dev.hddc.framework.security.filter

import dev.hddc.framework.security.authentication.UserAuthenticationDTO
import dev.hddc.framework.security.config.SecurityConfig
import dev.hddc.framework.security.jwt.JwtService
import dev.hddc.framework.security.jwt.spec.JwtSpec
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService,
) : OncePerRequestFilter() {

    private val pathMatcher = AntPathMatcher()

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        return SecurityConfig.SKIP_JWT_URLS.any { pathMatcher.match(it, path) }
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val token = resolveToken(request)

        if (token != null && jwtService.isValid(token)) {
            try {
                val username = jwtService.getUsername(token)
                val userDetails = userDetailsService.loadUserByUsername(username) as UserAuthenticationDTO

                val authentication = UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.authorities,
                )

                SecurityContextHolder.getContext().authentication = authentication
            } catch (_: Exception) {
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearer = request.getHeader(JwtSpec.TOKEN_HEADER) ?: return null
        return if (bearer.startsWith(JwtSpec.TOKEN_PREFIX)) bearer.removePrefix(JwtSpec.TOKEN_PREFIX) else null
    }
}
