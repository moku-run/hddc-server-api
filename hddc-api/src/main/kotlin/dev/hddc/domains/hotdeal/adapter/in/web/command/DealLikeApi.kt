package dev.hddc.domains.hotdeal.adapter.`in`.web.command

import dev.hddc.domains.hotdeal.application.ports.input.command.DealLikeUsecase
import dev.hddc.framework.api.response.ApiResponse
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.api.response.ApiResult
import dev.hddc.framework.security.authentication.UserAuthenticationDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Hot Deal Like", description = "핫딜 좋아요 API")
@RestController
class DealLikeApi(
    private val dealLikeUsecase: DealLikeUsecase,
) {
    @Operation(summary = "좋아요")
    @PostMapping("/api/hot-deals/{dealId}/likes")
    fun like(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @PathVariable dealId: Long,
    ): ApiResult<Nothing> {
        dealLikeUsecase.like(user.userId, dealId)
        return ApiResponse.of(ApiResponseCode.OK)
    }

    @Operation(summary = "좋아요 취소")
    @DeleteMapping("/api/hot-deals/{dealId}/likes")
    fun unlike(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @PathVariable dealId: Long,
    ): ApiResult<Nothing> {
        dealLikeUsecase.unlike(user.userId, dealId)
        return ApiResponse.of(ApiResponseCode.OK)
    }
}
