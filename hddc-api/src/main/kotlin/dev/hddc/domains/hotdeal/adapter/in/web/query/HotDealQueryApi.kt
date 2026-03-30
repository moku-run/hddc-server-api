package dev.hddc.domains.hotdeal.adapter.`in`.web.query

import dev.hddc.domains.hotdeal.adapter.`in`.web.response.CommentCursorResponse
import dev.hddc.domains.hotdeal.adapter.`in`.web.response.CommentResponse
import dev.hddc.domains.hotdeal.adapter.`in`.web.response.HotDealPageResponse
import dev.hddc.domains.hotdeal.adapter.`in`.web.response.HotDealResponse
import dev.hddc.domains.hotdeal.application.ports.input.query.HotDealPageResult
import dev.hddc.domains.hotdeal.application.ports.input.query.HotDealQueryUsecase
import dev.hddc.framework.api.response.ApiResponse
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.api.response.ApiResult
import dev.hddc.framework.security.authentication.UserAuthenticationDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
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
        @PageableDefault(size = 20) pageable: Pageable,
    ): ApiResult<HotDealPageResponse> =
        ApiResponse.of(ApiResponseCode.OK, hotDealQueryUsecase.getDeals(user?.userId, sort, pageable).toResponse())

    @Operation(summary = "딜 검색")
    @GetMapping("/api/hot-deals/search")
    fun search(
        @AuthenticationPrincipal user: UserAuthenticationDTO?,
        @RequestParam q: String,
        @PageableDefault(size = 20) pageable: Pageable,
    ): ApiResult<HotDealPageResponse> =
        ApiResponse.of(ApiResponseCode.OK, hotDealQueryUsecase.search(user?.userId, q, pageable).toResponse())

    @Operation(summary = "댓글 목록 조회 (커서 기반)")
    @GetMapping("/api/hot-deals/{dealId}/comments")
    fun getComments(
        @AuthenticationPrincipal user: UserAuthenticationDTO?,
        @PathVariable dealId: Long,
        @RequestParam(required = false) after: Long?,
        @RequestParam(defaultValue = "20") size: Int,
    ): ApiResult<CommentCursorResponse> {
        val result = hotDealQueryUsecase.getCommentsEnriched(dealId, user?.userId, after, size)
        val response = CommentCursorResponse(
            comments = result.comments.map {
                CommentResponse.from(it.comment, it.nickname, it.isLiked)
            },
            nextCursor = result.nextCursor,
            hasNext = result.hasNext,
        )
        return ApiResponse.of(ApiResponseCode.OK, response)
    }

    private fun HotDealPageResult.toResponse(): HotDealPageResponse =
        HotDealPageResponse(
            content = content.map {
                HotDealResponse.from(it.deal, it.nickname, it.dealNumber, it.isLiked, it.isVotedExpired, it.isClicked)
            },
            pagination = pagination,
        )
}
