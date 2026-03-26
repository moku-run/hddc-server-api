package dev.hddc.domains.hotdeal.adapter.`in`.web.command

import dev.hddc.domains.hotdeal.adapter.`in`.web.command.request.ReportRequest
import dev.hddc.domains.hotdeal.application.ports.input.command.DealReportUsecase
import dev.hddc.framework.api.response.ApiResponse
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.api.response.ApiResult
import dev.hddc.framework.security.authentication.UserAuthenticationDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Hot Deal Report", description = "핫딜 신고 API")
@RestController
class DealReportApi(
    private val dealReportUsecase: DealReportUsecase,
) {
    @Operation(summary = "딜 신고")
    @PostMapping("/api/hot-deals/{dealId}/reports")
    fun reportDeal(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @PathVariable dealId: Long,
        @Valid @RequestBody request: ReportRequest,
    ): ApiResult<Nothing> {
        dealReportUsecase.reportDeal(user.userId, dealId, request.reason)
        return ApiResponse.of(ApiResponseCode.CREATED)
    }

    @Operation(summary = "댓글 신고")
    @PostMapping("/api/hot-deals/{dealId}/comments/{commentId}/reports")
    fun reportComment(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @PathVariable dealId: Long,
        @PathVariable commentId: Long,
        @Valid @RequestBody request: ReportRequest,
    ): ApiResult<Nothing> {
        dealReportUsecase.reportComment(user.userId, dealId, commentId, request.reason)
        return ApiResponse.of(ApiResponseCode.CREATED)
    }
}
