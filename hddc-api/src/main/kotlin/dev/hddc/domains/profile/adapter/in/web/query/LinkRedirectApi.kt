package dev.hddc.domains.profile.adapter.`in`.web.query

import dev.hddc.domains.profile.application.ports.output.command.ProfileLinkCommandPort
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Link Redirect", description = "링크 리다이렉트")
@RestController
class LinkRedirectApi(
    private val profileLinkCommandPort: ProfileLinkCommandPort,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "링크 클릭 → 리다이렉트")
    @GetMapping("/r/{linkId}")
    fun redirect(
        @PathVariable linkId: Long,
        request: HttpServletRequest,
    ): ResponseEntity<Void> {
        val link = profileLinkCommandPort.findById(linkId)
            ?: return ResponseEntity.notFound().build()

        if (!link.enabled || link.isDeleted) {
            return ResponseEntity.notFound().build()
        }

        val ip = request.getHeader("X-Forwarded-For")?.split(",")?.first()?.trim()
            ?: request.remoteAddr
        val userAgent = request.getHeader("User-Agent") ?: "-"
        val referer = request.getHeader("Referer") ?: "-"
        val acceptLanguage = request.getHeader("Accept-Language")?.take(20) ?: "-"

        log.info("[CLICK] linkId={} → {} | ip={} | lang={} | referer={} | ua={}",
            linkId, link.url, ip, acceptLanguage, referer, userAgent)

        return ResponseEntity.status(HttpStatus.FOUND)
            .header(HttpHeaders.LOCATION, link.url)
            .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
            .header(HttpHeaders.PRAGMA, "no-cache")
            .header(HttpHeaders.EXPIRES, "0")
            .build()
    }
}
