package dev.hddc.domains.hotdeal.adapter.`in`.web.query

import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealClickPort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommandPort
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
    private val hotDealCommandPort: HotDealCommandPort,
    private val hotDealClickPort: HotDealClickPort,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "핫딜 클릭 → 리다이렉트")
    @GetMapping("/r/deals/{dealId}")
    fun redirect(
        @PathVariable dealId: Long,
        @AuthenticationPrincipal user: UserAuthenticationDTO?,
        request: HttpServletRequest,
    ): ResponseEntity<Void> {
        val deal = hotDealCommandPort.findById(dealId)
            ?: return ResponseEntity.notFound().build()

        if (deal.isDeleted || deal.isExpired) {
            return ResponseEntity.notFound().build()
        }

        val ip = request.getHeader("X-Forwarded-For")?.split(",")?.first()?.trim()
            ?: request.remoteAddr
        val userId = user?.userId

        if (!hotDealClickPort.isDuplicate(dealId, userId, ip)) {
            hotDealClickPort.save(dealId, userId, ip)
            hotDealCommandPort.save(deal.copy(clickCount = deal.clickCount + 1))
        }

        log.info("[DEAL_CLICK] dealId={} → {} | userId={} | ip={}",
            dealId, deal.url, userId ?: "anonymous", ip)

        return ResponseEntity.status(HttpStatus.FOUND)
            .header(HttpHeaders.LOCATION, deal.url)
            .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
            .header(HttpHeaders.PRAGMA, "no-cache")
            .header(HttpHeaders.EXPIRES, "0")
            .build()
    }
}
