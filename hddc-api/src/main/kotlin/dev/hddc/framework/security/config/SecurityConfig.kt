package dev.hddc.framework.security.config

import dev.hddc.framework.security.filter.JwtAuthenticationFilter
import dev.hddc.framework.security.handler.ApiAccessDeniedHandler
import dev.hddc.framework.security.handler.ApiAuthenticationEntryPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val apiAuthenticationEntryPoint: ApiAuthenticationEntryPoint,
    private val apiAccessDeniedHandler: ApiAccessDeniedHandler,
    @org.springframework.beans.factory.annotation.Value("\${cors.allowed-origins:http://localhost:3000}")
    private val corsAllowedOrigins: String,
) {
    companion object {
        val PERMIT_ALL_URLS = arrayOf(
            "/api/auth/email-verifications",
            "/api/auth/email-verifications/verify",
            "/api/auth/sign-up",
            "/api/auth/login",
            "/api/auth/check-nickname",
            "/api/auth/logout",
            "/api/auth/refresh",
            "/api/auth/password-reset/**",
            "/api/admin/auth/login",
            "/api/click",
            "/api/view",
            "/r/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/actuator/health",
            "/actuator/prometheus",
            "/public/**",
        )

        val SKIP_JWT_URLS = arrayOf(
            "/api/auth/**",
            "/api/admin/auth/**",
            "/api/profiles/curated",
            "/api/click",
            "/api/view",
            "/api/events/stream",
            "/r/{linkId}",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/actuator/**",
            "/public/**",
        )
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .cors { it.configurationSource(corsConfigurationSource()) }
            .authorizeHttpRequests {
                it
                    .requestMatchers(HttpMethod.GET, "/api/profiles/{slug}").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/hot-deals").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/hot-deals/search").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/hot-deals/*/comments").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/profiles/curated").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/upload/presigned-url").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/events/stream").permitAll()
                    .requestMatchers(*PERMIT_ALL_URLS).permitAll()
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers("/api/admin/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MODERATOR")
                    .anyRequest().authenticated()
            }
            .exceptionHandling {
                it
                    .authenticationEntryPoint(apiAuthenticationEntryPoint)
                    .accessDeniedHandler(apiAccessDeniedHandler)
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration().apply {
            allowedOriginPatterns = corsAllowedOrigins.split(",").map { it.trim() }
            allowedMethods = listOf("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            allowedHeaders = listOf("*")
            exposedHeaders = listOf("Authorization", "Set-Cookie")
            allowCredentials = true
        }

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", config)
        }
    }
}
