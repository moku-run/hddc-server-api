package dev.hddc.domains.hotdeal.adapter.`in`.web.command

import dev.hddc.domains.hotdeal.application.ports.input.command.DealExpiredVoteUsecase
import dev.hddc.framework.api.response.ApiResponse
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.security.authentication.UserAuthenticationDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Hot Deal Expired Vote", description = "핫딜 종료 투표 API")
@RestController
class DealExpiredVoteApi(
    private val dealExpiredVoteUsecase: DealExpiredVoteUsecase,
) {
    @Operation(summary = "종료됐어요 투표")
    @PostMapping("/api/hot-deals/{dealId}/expired-votes")
    fun vote(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @PathVariable dealId: Long,
    ): ResponseEntity<ApiResponse<Nothing>> {
        dealExpiredVoteUsecase.vote(user.userId, dealId)
        return ApiResponse.of(ApiResponseCode.OK)
    }

    @Operation(summary = "투표 취소")
    @DeleteMapping("/api/hot-deals/{dealId}/expired-votes")
    fun unvote(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @PathVariable dealId: Long,
    ): ResponseEntity<ApiResponse<Nothing>> {
        dealExpiredVoteUsecase.unvote(user.userId, dealId)
        return ApiResponse.of(ApiResponseCode.OK)
    }
}
