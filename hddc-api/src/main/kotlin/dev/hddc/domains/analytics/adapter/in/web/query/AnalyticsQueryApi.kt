package dev.hddc.domains.analytics.adapter.`in`.web.query

import dev.hddc.domains.analytics.application.ports.input.query.AnalyticsSummary
import dev.hddc.domains.analytics.application.ports.input.query.DailyAnalytics
import dev.hddc.domains.analytics.application.ports.input.query.GetAnalyticsUsecase
import dev.hddc.domains.analytics.application.ports.input.query.TopLink
import dev.hddc.framework.api.response.ApiResponse
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.security.authentication.UserAuthenticationDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Analytics Query", description = "분석 대시보드 API")
@RestController
class AnalyticsQueryApi(
    private val getAnalyticsUsecase: GetAnalyticsUsecase,
) {
    @Operation(summary = "요약 통계")
    @GetMapping("/api/profiles/me/analytics/summary")
    fun getSummary(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @RequestParam(defaultValue = "7d") period: String,
    ): ResponseEntity<ApiResponse<AnalyticsSummary>> =
        ApiResponse.of(ApiResponseCode.OK, getAnalyticsUsecase.getSummary(user.userId, period))

    @Operation(summary = "일별 추이 데이터")
    @GetMapping("/api/profiles/me/analytics/daily")
    fun getDaily(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @RequestParam(defaultValue = "7d") period: String,
    ): ResponseEntity<ApiResponse<List<DailyAnalytics>>> =
        ApiResponse.of(ApiResponseCode.OK, getAnalyticsUsecase.getDaily(user.userId, period))

    @Operation(summary = "상위 클릭 링크")
    @GetMapping("/api/profiles/me/analytics/top-links")
    fun getTopLinks(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @RequestParam(defaultValue = "5") limit: Int,
    ): ResponseEntity<ApiResponse<List<TopLink>>> =
        ApiResponse.of(ApiResponseCode.OK, getAnalyticsUsecase.getTopLinks(user.userId, limit))
}
