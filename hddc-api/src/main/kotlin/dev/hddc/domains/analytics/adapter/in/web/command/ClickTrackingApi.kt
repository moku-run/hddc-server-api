package dev.hddc.domains.analytics.adapter.`in`.web.command

import dev.hddc.domains.analytics.adapter.`in`.web.command.request.TrackClickRequest
import dev.hddc.domains.analytics.adapter.`in`.web.command.request.TrackViewRequest
import dev.hddc.domains.analytics.application.ports.input.command.TrackClickUsecase
import dev.hddc.framework.api.response.ApiResponse
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.api.response.ApiResult
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Click Tracking", description = "클릭 트래킹 API (인증 불필요)")
@RestController
class ClickTrackingApi(
    private val trackClickUsecase: TrackClickUsecase,
) {
    @Operation(summary = "링크 클릭 트래킹")
    @PostMapping("/api/click")
    fun trackClick(
        @Valid @RequestBody request: TrackClickRequest,
        httpRequest: HttpServletRequest,
    ): ApiResult<Nothing> {
        trackClickUsecase.trackClick(
            slug = request.slug,
            linkId = request.linkId,
            ip = httpRequest.remoteAddr,
            userAgent = httpRequest.getHeader("User-Agent"),
            referer = httpRequest.getHeader("Referer"),
        )
        return ApiResponse.of(ApiResponseCode.OK)
    }

    @Operation(summary = "페이지 조회 트래킹")
    @PostMapping("/api/view")
    fun trackView(
        @Valid @RequestBody request: TrackViewRequest,
        httpRequest: HttpServletRequest,
    ): ApiResult<Nothing> {
        trackClickUsecase.trackPageView(
            slug = request.slug,
            ip = httpRequest.remoteAddr,
            userAgent = httpRequest.getHeader("User-Agent"),
            referer = httpRequest.getHeader("Referer"),
        )
        return ApiResponse.of(ApiResponseCode.OK)
    }
}