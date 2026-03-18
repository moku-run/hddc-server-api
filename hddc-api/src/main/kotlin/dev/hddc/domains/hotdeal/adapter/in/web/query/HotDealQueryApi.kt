package dev.hddc.domains.hotdeal.adapter.`in`.web.query

import dev.hddc.domains.hotdeal.adapter.`in`.web.response.CommentResponse
import dev.hddc.domains.hotdeal.adapter.`in`.web.response.HotDealPageResponse
import dev.hddc.domains.hotdeal.adapter.`in`.web.response.HotDealResponse
import dev.hddc.domains.hotdeal.application.ports.input.query.HotDealPageResult
import dev.hddc.domains.hotdeal.application.ports.input.query.HotDealQueryUsecase
import dev.hddc.framework.api.response.ApiResponse
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.security.authentication.UserAuthenticationDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Hot Deal Query", description = "핫딜 조회 API")
@RestController
class HotDealQueryApi(
    private val hotDealQueryUsecase: HotDealQueryUsecase,
) {
    @Operation(summary = "딜 목록 조회")
    @GetMapping("/api/hot-deals")
    fun getDeals(
        @AuthenticationPrincipal user: UserAuthenticationDTO?,
        @RequestParam(defaultValue = "latest") sort: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") limit: Int,
    ): ResponseEntity<ApiResponse<HotDealPageResponse>> =
        ApiResponse.of(ApiResponseCode.OK, hotDealQueryUsecase.getDeals(user?.userId, sort, page, limit).toResponse())

    @Operation(summary = "딜 검색")
    @GetMapping("/api/hot-deals/search")
    fun search(
        @AuthenticationPrincipal user: UserAuthenticationDTO?,
        @RequestParam q: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") limit: Int,
    ): ResponseEntity<ApiResponse<HotDealPageResponse>> =
        ApiResponse.of(ApiResponseCode.OK, hotDealQueryUsecase.search(user?.userId, q, page, limit).toResponse())

    @Operation(summary = "댓글 목록 조회")
    @GetMapping("/api/hot-deals/{dealId}/comments")
    fun getComments(
        @PathVariable dealId: Long,
    ): ResponseEntity<ApiResponse<List<CommentResponse>>> =
        ApiResponse.of(ApiResponseCode.OK, hotDealQueryUsecase.getComments(dealId).map { CommentResponse.from(it) })

    private fun HotDealPageResult.toResponse() = HotDealPageResponse(
        content = content.map { HotDealResponse.from(it.deal, it.isLiked, it.isVotedExpired) },
        page = page,
        size = size,
        totalElements = totalElements,
        totalPages = totalPages,
    )
}
