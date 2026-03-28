package dev.hddc.domains.hotdeal.adapter.`in`.web.command

import dev.hddc.domains.hotdeal.application.ports.input.command.DealClickUsecase
import dev.hddc.framework.security.authentication.UserAuthenticationDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Deal Redirect", description = "핫딜 리다이렉트")
@RestController
class DealRedirectApi(
    private val dealClickUsecase: DealClickUsecase,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "핫딜 클릭 → 리다이렉트")
    @GetMapping("/r/deals/{dealId}")
    fun redirect(
        @PathVariable dealId: Long,
        @AuthenticationPrincipal user: UserAuthenticationDTO?,
        request: HttpServletRequest,
    ): ResponseEntity<Void> {
        val ip = request.getHeader("X-Forwarded-For")?.split(",")?.first()?.trim()
            ?: request.remoteAddr
        val userId = user?.userId

        val result = dealClickUsecase.click(dealId, userId, ip)
            ?: return ResponseEntity.notFound().build()

        log.info("[DEAL_CLICK] dealId={} → {} | userId={} | ip={}",
            dealId, result.url, userId ?: "anonymous", ip)

        return ResponseEntity.status(HttpStatus.FOUND)
            .header(HttpHeaders.LOCATION, result.url)
            .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
            .header(HttpHeaders.PRAGMA, "no-cache")
            .header(HttpHeaders.EXPIRES, "0")
            .build()
    }
}
