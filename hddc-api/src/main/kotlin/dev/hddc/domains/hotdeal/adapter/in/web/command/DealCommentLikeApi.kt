package dev.hddc.domains.hotdeal.adapter.`in`.web.command

import dev.hddc.domains.hotdeal.application.ports.input.command.DealCommentLikeUsecase
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

@Tag(name = "Hot Deal Comment Like", description = "핫딜 댓글 좋아요 API")
@RestController
class DealCommentLikeApi(
    private val dealCommentLikeUsecase: DealCommentLikeUsecase,
) {
    @Operation(summary = "댓글 좋아요")
    @PostMapping("/api/hot-deals/{dealId}/comments/{commentId}/likes")
    fun like(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @PathVariable dealId: Long,
        @PathVariable commentId: Long,
    ): ResponseEntity<ApiResponse<Nothing>> {
        dealCommentLikeUsecase.like(user.userId, commentId)
        return ApiResponse.of(ApiResponseCode.OK)
    }

    @Operation(summary = "댓글 좋아요 취소")
    @DeleteMapping("/api/hot-deals/{dealId}/comments/{commentId}/likes")
    fun unlike(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @PathVariable dealId: Long,
        @PathVariable commentId: Long,
    ): ResponseEntity<ApiResponse<Nothing>> {
        dealCommentLikeUsecase.unlike(user.userId, commentId)
        return ApiResponse.of(ApiResponseCode.OK)
    }
}
