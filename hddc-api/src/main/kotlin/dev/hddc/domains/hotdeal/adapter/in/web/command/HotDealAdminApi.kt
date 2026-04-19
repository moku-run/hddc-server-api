package dev.hddc.domains.hotdeal.adapter.`in`.web.command

import dev.hddc.domains.hotdeal.adapter.`in`.web.command.request.CreateHotDealRequest
import dev.hddc.domains.hotdeal.adapter.`in`.web.command.request.UpdateHotDealRequest
import dev.hddc.domains.hotdeal.adapter.`in`.web.response.HotDealAdminResponse
import dev.hddc.domains.hotdeal.application.ports.input.command.HotDealAdminUsecase
import dev.hddc.framework.api.response.ApiResponse
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.api.response.ApiResult
import dev.hddc.framework.security.authentication.UserAuthenticationDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@Tag(name = "Admin - Hot Deal", description = "관리자 핫딜 관리 API")
@RestController
class HotDealAdminApi(
    private val hotDealAdminUsecase: HotDealAdminUsecase,
) {
    @Operation(summary = "핫딜 직접 등록")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MODERATOR')")
    @PostMapping("/api/admin/hot-deals")
    fun create(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @Valid @RequestBody request: CreateHotDealRequest,
    ): ApiResult<HotDealAdminResponse> {
        val deal = hotDealAdminUsecase.create(
            adminUserId = user.userId,
            command = request.toCommand(),
        )
        return ApiResponse.of(ApiResponseCode.CREATED, HotDealAdminResponse.from(deal, user.nickname))
    }

    @Operation(summary = "핫딜 수정 (originalUrl 포함)")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MODERATOR')")
    @PatchMapping("/api/admin/hot-deals/{dealId}")
    fun update(
        @PathVariable dealId: Long,
        @RequestBody request: UpdateHotDealRequest,
    ): ApiResult<HotDealAdminResponse> {
        val result = hotDealAdminUsecase.update(
            dealId = dealId,
            command = request.toCommand(),
        )
        return ApiResponse.of(ApiResponseCode.OK, HotDealAdminResponse.from(result.deal, result.nickname))
    }

    @Operation(summary = "핫딜 삭제 (soft delete)")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MODERATOR')")
    @DeleteMapping("/api/admin/hot-deals/{dealId}")
    fun delete(
        @PathVariable dealId: Long,
    ): ApiResult<Nothing> {
        hotDealAdminUsecase.delete(dealId)
        return ApiResponse.of(ApiResponseCode.DELETED)
    }
}
