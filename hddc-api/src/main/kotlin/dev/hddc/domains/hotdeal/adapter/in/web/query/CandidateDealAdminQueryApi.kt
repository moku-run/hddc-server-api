package dev.hddc.domains.hotdeal.adapter.`in`.web.query

import dev.hddc.domains.hotdeal.adapter.`in`.web.response.CandidateDealPageResponse
import dev.hddc.domains.hotdeal.application.ports.input.query.CandidateDealAdminQueryUsecase
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Admin - Candidate Deal Query", description = "관리자 후보 딜 조회 API")
@RestController
class CandidateDealAdminQueryApi(
    private val candidateDealAdminQueryUsecase: CandidateDealAdminQueryUsecase,
) {
    @Operation(summary = "후보 딜 목록 조회")
    @GetMapping("/api/admin/candidate-deals")
    fun getCandidateDeals(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @RequestParam(defaultValue = "PENDING") status: String,
        @PageableDefault(size = 20, sort = ["crawledAt"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ApiResult<CandidateDealPageResponse> {
        val result = candidateDealAdminQueryUsecase.getCandidateDeals(status, pageable.pageNumber, pageable.pageSize)
        return ApiResponse.of(ApiResponseCode.OK, CandidateDealPageResponse.from(result))
    }
}
