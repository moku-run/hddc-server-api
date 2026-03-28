package dev.hddc.domains.hotdeal.adapter.`in`.web.command

import dev.hddc.domains.hotdeal.adapter.`in`.web.command.request.CreateHotDealRequest
import dev.hddc.domains.hotdeal.adapter.`in`.web.command.request.UpdateHotDealRequest
import dev.hddc.domains.hotdeal.adapter.`in`.web.response.HotDealPageResponse
import dev.hddc.domains.hotdeal.adapter.`in`.web.response.HotDealResponse
import dev.hddc.domains.hotdeal.application.ports.input.command.CreateHotDealCommand
import dev.hddc.domains.hotdeal.application.ports.input.command.HotDealAdminUsecase
import dev.hddc.domains.hotdeal.application.ports.input.command.UpdateHotDealCommand
import dev.hddc.domains.hotdeal.application.ports.input.query.HotDealAdminQueryUsecase
import dev.hddc.framework.api.response.ApiResponse
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.api.response.ApiResult
import dev.hddc.framework.security.authentication.UserAuthenticationDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@Tag(name = "Admin - Hot Deal", description = "관리자 핫딜 관리 API")
@RestController
class HotDealAdminApi(
    private val hotDealAdminQueryUsecase: HotDealAdminQueryUsecase,
    private val hotDealAdminUsecase: HotDealAdminUsecase,
) {
    @Operation(summary = "핫딜 전체 목록 (삭제 포함)")
    @GetMapping("/api/admin/hot-deals")
    fun getAll(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @PageableDefault(size = 20, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ApiResult<HotDealPageResponse> {
        val result = hotDealAdminQueryUsecase.getAll(pageable.pageNumber, pageable.pageSize)
        val response = HotDealPageResponse(
            content = result.content.map { it ->
                HotDealResponse.from(it.deal, it.nickname, it.dealNumber)
            },
            page = result.page,
            size = result.size,
            totalElements = result.totalElements,
            totalPages = result.totalPages,
        )
        return ApiResponse.of(ApiResponseCode.OK, response)
    }

    @Operation(summary = "핫딜 직접 등록")
    @PostMapping("/api/admin/hot-deals")
    fun create(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @Valid @RequestBody request: CreateHotDealRequest,
    ): ApiResult<HotDealResponse> {
        val deal = hotDealAdminUsecase.create(
            adminUserId = user.userId,
            command = CreateHotDealCommand(
                title = request.title,
                description = request.description,
                url = request.url,
                imageUrl = request.imageUrl,
                originalPrice = request.originalPrice,
                dealPrice = request.dealPrice,
                discountRate = request.discountRate,
                category = request.category,
                store = request.store,
            ),
        )
        return ApiResponse.of(ApiResponseCode.CREATED, HotDealResponse.from(deal, user.nickname))
    }

    @Operation(summary = "핫딜 수정")
    @PatchMapping("/api/admin/hot-deals/{dealId}")
    fun update(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @PathVariable dealId: Long,
        @RequestBody request: UpdateHotDealRequest,
    ): ApiResult<HotDealResponse> {
        val result = hotDealAdminUsecase.update(
            dealId = dealId,
            command = UpdateHotDealCommand(
                title = request.title,
                description = request.description,
                url = request.url,
                imageUrl = request.imageUrl,
                originalPrice = request.originalPrice,
                dealPrice = request.dealPrice,
                discountRate = request.discountRate,
                category = request.category,
                store = request.store,
            ),
        )
        return ApiResponse.of(ApiResponseCode.OK, HotDealResponse.from(result.deal, result.nickname))
    }

    @Operation(summary = "핫딜 삭제 (soft delete)")
    @DeleteMapping("/api/admin/hot-deals/{dealId}")
    fun delete(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @PathVariable dealId: Long,
    ): ApiResult<Nothing> {
        hotDealAdminUsecase.delete(dealId)
        return ApiResponse.of(ApiResponseCode.DELETED)
    }
}
