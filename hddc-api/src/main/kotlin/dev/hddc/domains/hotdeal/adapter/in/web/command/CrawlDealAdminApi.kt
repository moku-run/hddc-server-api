package dev.hddc.domains.hotdeal.adapter.`in`.web.command

import dev.hddc.domains.hotdeal.application.ports.input.command.ApproveResult
import dev.hddc.domains.hotdeal.application.ports.input.command.CrawlDealAdminUsecase
import dev.hddc.domains.hotdeal.domain.model.CrawlHotDealModel
import dev.hddc.framework.api.response.ApiResponse
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.security.authentication.UserAuthenticationDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

data class CrawlDealResponse(
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
        fun from(model: CrawlHotDealModel) = CrawlDealResponse(
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

data class CrawlDealPageResponse(
    val content: List<CrawlDealResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
)

data class BulkApproveRequest(val ids: List<Long>)

@Tag(name = "Admin - Crawl Deal", description = "관리자 크롤링 딜 관리 API")
@RestController
class CrawlDealAdminApi(
    private val crawlDealAdminUsecase: CrawlDealAdminUsecase,
) {
    @Operation(summary = "크롤링 딜 목록 조회")
    @GetMapping("/api/admin/crawl-deals")
    fun getCrawlDeals(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @RequestParam(defaultValue = "PENDING") status: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<ApiResponse<CrawlDealPageResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "crawledAt"))
        val result = crawlDealAdminUsecase.getCrawlDeals(status, pageable)
        return ApiResponse.of(ApiResponseCode.OK, result.toResponse())
    }

    @Operation(summary = "크롤링 딜 승인 → mst_hot_deal로 이전")
    @PostMapping("/api/admin/crawl-deals/{id}/approve")
    fun approve(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @PathVariable id: Long,
    ): ResponseEntity<ApiResponse<Map<String, Long>>> =
        ApiResponse.of(ApiResponseCode.CREATED, mapOf("hotDealId" to crawlDealAdminUsecase.approve(id)))

    @Operation(summary = "크롤링 딜 거부")
    @PostMapping("/api/admin/crawl-deals/{id}/reject")
    fun reject(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @PathVariable id: Long,
    ): ResponseEntity<ApiResponse<Nothing>> {
        crawlDealAdminUsecase.reject(id)
        return ApiResponse.of(ApiResponseCode.OK)
    }

    @Operation(summary = "크롤링 딜 일괄 승인")
    @PostMapping("/api/admin/crawl-deals/bulk-approve")
    fun bulkApprove(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @RequestBody request: BulkApproveRequest,
    ): ResponseEntity<ApiResponse<ApproveResult>> =
        ApiResponse.of(ApiResponseCode.CREATED, crawlDealAdminUsecase.bulkApprove(request.ids))

    private fun Page<CrawlHotDealModel>.toResponse() = CrawlDealPageResponse(
        content = content.map { CrawlDealResponse.from(it) },
        page = number,
        size = size,
        totalElements = totalElements,
        totalPages = totalPages,
    )
}
