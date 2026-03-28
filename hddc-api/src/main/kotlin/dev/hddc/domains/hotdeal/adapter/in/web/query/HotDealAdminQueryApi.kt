package dev.hddc.domains.hotdeal.adapter.`in`.web.query

import dev.hddc.domains.hotdeal.adapter.`in`.web.response.HotDealPageResponse
import dev.hddc.domains.hotdeal.adapter.`in`.web.response.HotDealResponse
import dev.hddc.domains.hotdeal.application.ports.input.query.HotDealAdminQueryUsecase
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
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Admin - Hot Deal Query", description = "관리자 핫딜 조회 API")
@RestController
class HotDealAdminQueryApi(
    private val hotDealAdminQueryUsecase: HotDealAdminQueryUsecase,
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
            pagination = result.pagination,
        )
        return ApiResponse.of(ApiResponseCode.OK, response)
    }
}
