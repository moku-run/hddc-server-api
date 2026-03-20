package dev.hddc.domains.profile.adapter.`in`.web.command

import dev.hddc.domains.profile.application.ports.input.command.ProfileReportUsecase
import dev.hddc.framework.api.response.ApiResponse
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.security.authentication.UserAuthenticationDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

data class ReportRequest(
    @field:NotBlank(message = "신고 사유는 필수입니다.")
    @field:Size(max = 100, message = "신고 사유는 100자 이하여야 합니다.")
    val reason: String,
)

@Tag(name = "Profile Report", description = "프로필 신고 API")
@RestController
class ProfileReportApi(
    private val profileReportUsecase: ProfileReportUsecase,
) {
    @Operation(summary = "프로필 신고")
    @PostMapping("/api/profiles/{slug}/reports")
    fun reportProfile(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @PathVariable slug: String,
        @Valid @RequestBody request: ReportRequest,
    ): ResponseEntity<ApiResponse<Nothing>> {
        profileReportUsecase.reportProfile(user.userId, slug, request.reason)
        return ApiResponse.of(ApiResponseCode.CREATED)
    }

    @Operation(summary = "프로필 링크 신고")
    @PostMapping("/api/profiles/{slug}/links/{linkId}/reports")
    fun reportLink(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @PathVariable slug: String,
        @PathVariable linkId: Long,
        @Valid @RequestBody request: ReportRequest,
    ): ResponseEntity<ApiResponse<Nothing>> {
        profileReportUsecase.reportLink(user.userId, slug, linkId, request.reason)
        return ApiResponse.of(ApiResponseCode.CREATED)
    }
}
