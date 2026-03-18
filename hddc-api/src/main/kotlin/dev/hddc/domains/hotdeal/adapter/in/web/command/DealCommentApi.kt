package dev.hddc.domains.hotdeal.adapter.`in`.web.command

import dev.hddc.domains.hotdeal.adapter.`in`.web.command.request.AddCommentRequest
import dev.hddc.domains.hotdeal.adapter.`in`.web.response.CommentResponse
import dev.hddc.domains.hotdeal.application.ports.input.command.DealCommentUsecase
import dev.hddc.framework.api.response.ApiResponse
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.security.authentication.UserAuthenticationDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Hot Deal Comment", description = "핫딜 댓글 API")
@RestController
class DealCommentApi(
    private val dealCommentUsecase: DealCommentUsecase,
) {
    @Operation(summary = "댓글 작성")
    @PostMapping("/api/hot-deals/{dealId}/comments")
    fun addComment(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @PathVariable dealId: Long,
        @Valid @RequestBody request: AddCommentRequest,
    ): ResponseEntity<ApiResponse<CommentResponse>> =
        ApiResponse.of(
            ApiResponseCode.CREATED,
            CommentResponse.from(dealCommentUsecase.addComment(user.userId, dealId, request.content, request.parentId)),
        )

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/api/hot-deals/{dealId}/comments/{commentId}")
    fun deleteComment(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @PathVariable dealId: Long,
        @PathVariable commentId: Long,
    ): ResponseEntity<ApiResponse<Nothing>> {
        dealCommentUsecase.deleteComment(user.userId, dealId, commentId)
        return ApiResponse.of(ApiResponseCode.DELETED)
    }
}
