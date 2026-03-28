package dev.hddc.domains.hotdeal.adapter.`in`.web.command

import dev.hddc.domains.hotdeal.application.ports.input.command.ApproveResult
import dev.hddc.domains.hotdeal.application.ports.input.command.CandidateDealAdminUsecase
import dev.hddc.domains.hotdeal.application.ports.output.command.CandidateDealPageData
import dev.hddc.domains.hotdeal.domain.model.CandidateDealModel
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

data class CandidateDealResponse(
    val id: Long,
    val sourceSite: String,
    val sourceId: String?,
    val title: String?,
    val description: String?,
    val postUrl: String,
    val dealLink: String?,
    val imageUrl: String?,
    val originalPrice: Int?,
    val dealPrice: Int?,
    val discountRate: Int?,
    val store: String?,
    val category: String?,
    val status: String,
    val crawledAt: String,
    val transferredAt: String?,
) {
    companion object {
        fun from(model: CandidateDealModel) = CandidateDealResponse(
            id = model.id!!,
            sourceSite = model.sourceSite,
            sourceId = model.sourceId,
            title = model.title,
            description = model.description,
            postUrl = model.postUrl,
            dealLink = model.dealLink,
            imageUrl = model.imageUrl,
            originalPrice = model.originalPrice,
            dealPrice = model.dealPrice,
            discountRate = model.discountRate,
            store = model.store,
            category = model.category,
            status = model.status,
            crawledAt = model.crawledAt.toString(),
            transferredAt = model.transferredAt?.toString(),
        )
    }
}

data class CandidateDealPageResponse(
    val content: List<CandidateDealResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
)

data class BulkApproveRequest(val ids: List<Long>)

@Tag(name = "Admin - Candidate Deal", description = "관리자 후보 딜 관리 API")
@RestController
class CandidateDealAdminApi(
    private val candidateDealAdminUsecase: CandidateDealAdminUsecase,
) {
    @Operation(summary = "후보 딜 목록 조회")
    @GetMapping("/api/admin/candidate-deals")
    fun getCandidateDeals(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @RequestParam(defaultValue = "PENDING") status: String,
        @PageableDefault(size = 20, sort = ["crawledAt"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ApiResult<CandidateDealPageResponse> {
        val result = candidateDealAdminUsecase.getCandidateDeals(status, pageable.pageNumber, pageable.pageSize)
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
