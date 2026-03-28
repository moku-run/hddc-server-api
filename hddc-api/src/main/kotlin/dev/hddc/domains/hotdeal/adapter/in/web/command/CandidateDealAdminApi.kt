package dev.hddc.domains.hotdeal.adapter.`in`.web.command

import dev.hddc.domains.hotdeal.adapter.`in`.web.command.request.BulkApproveRequest
import dev.hddc.domains.hotdeal.adapter.`in`.web.response.CandidateDealPageResponse
import dev.hddc.domains.hotdeal.adapter.`in`.web.response.CandidateDealResponse
import dev.hddc.domains.hotdeal.application.ports.input.command.ApproveResult
import dev.hddc.domains.hotdeal.application.ports.input.command.CandidateDealAdminUsecase
import dev.hddc.domains.hotdeal.application.ports.input.query.CandidateDealAdminQueryUsecase
import dev.hddc.domains.hotdeal.application.ports.output.query.CandidateDealPageData
import dev.hddc.framework.api.response.ApiResponse
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.api.response.ApiResult
import dev.hddc.framework.security.authentication.UserAuthenticationDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@Tag(name = "Admin - Candidate Deal", description = "관리자 후보 딜 관리 API")
@RestController
class CandidateDealAdminApi(
    private val candidateDealAdminQueryUsecase: CandidateDealAdminQueryUsecase,
    private val candidateDealAdminUsecase: CandidateDealAdminUsecase,
) {
    @Operation(summary = "후보 딜 목록 조회")
    @GetMapping("/api/admin/candidate-deals")
    fun getCandidateDeals(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @RequestParam(defaultValue = "PENDING") status: String,
        @PageableDefault(size = 20, sort = ["crawledAt"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ApiResult<CandidateDealPageResponse> {
        val result = candidateDealAdminQueryUsecase.getCandidateDeals(status, pageable.pageNumber, pageable.pageSize)
        return ApiResponse.of(ApiResponseCode.OK, result.toResponse())
    }

    @Operation(summary = "후보 딜 승인 → mst_hot_deal로 이전")
    @PostMapping("/api/admin/candidate-deals/{id}/approve")
    fun approve(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @PathVariable id: Long,
    ): ApiResult<Map<String, Long>> =
        ApiResponse.of(ApiResponseCode.CREATED, mapOf("hotDealId" to candidateDealAdminUsecase.approve(id)))

    @Operation(summary = "후보 딜 거부")
    @PostMapping("/api/admin/candidate-deals/{id}/reject")
    fun reject(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @PathVariable id: Long,
    ): ApiResult<Nothing> {
        candidateDealAdminUsecase.reject(id)
        return ApiResponse.of(ApiResponseCode.OK)
    }

    @Operation(summary = "후보 딜 일괄 승인")
    @PostMapping("/api/admin/candidate-deals/bulk-approve")
    fun bulkApprove(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @RequestBody request: BulkApproveRequest,
    ): ApiResult<ApproveResult> =
        ApiResponse.of(ApiResponseCode.CREATED, candidateDealAdminUsecase.bulkApprove(request.ids))

    private fun CandidateDealPageData.toResponse() = CandidateDealPageResponse(
        content = content.map { CandidateDealResponse.from(it) },
        page = page,
        size = size,
        totalElements = totalElements,
        totalPages = totalPages,
    )
}
