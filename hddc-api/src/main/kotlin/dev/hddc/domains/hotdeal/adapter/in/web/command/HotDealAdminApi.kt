package dev.hddc.domains.hotdeal.adapter.`in`.web.command

import dev.hddc.domains.hotdeal.adapter.`in`.web.response.HotDealPageResponse
import dev.hddc.domains.hotdeal.adapter.`in`.web.response.HotDealResponse
import dev.hddc.domains.hotdeal.application.ports.input.command.CreateHotDealCommand
import dev.hddc.domains.hotdeal.application.ports.input.command.HotDealAdminUsecase
import dev.hddc.domains.hotdeal.application.ports.input.command.UpdateHotDealCommand
import dev.hddc.domains.user.application.ports.output.query.UserQueryPort
import dev.hddc.framework.api.response.ApiResponse
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.api.response.ApiResult
import dev.hddc.framework.security.authentication.UserAuthenticationDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

data class CreateHotDealRequest(
    @field:NotBlank(message = "제목은 필수입니다.")
    @field:Size(max = 200, message = "제목은 200자 이하여야 합니다.")
    val title: String,
    val description: String? = null,
    @field:NotBlank(message = "URL은 필수입니다.")
    val url: String,
    val imageUrl: String? = null,
    val originalPrice: Int? = null,
    val dealPrice: Int? = null,
    val discountRate: Int? = null,
    val category: String? = null,
    val store: String? = null,
)

data class UpdateHotDealRequest(
    val title: String? = null,
    val description: String? = null,
    val url: String? = null,
    val imageUrl: String? = null,
    val originalPrice: Int? = null,
    val dealPrice: Int? = null,
    val discountRate: Int? = null,
    val category: String? = null,
    val store: String? = null,
)

@Tag(name = "Admin - Hot Deal", description = "관리자 핫딜 관리 API")
@RestController
class HotDealAdminApi(
    private val hotDealAdminUsecase: HotDealAdminUsecase,
    private val userQueryPort: UserQueryPort,
) {
    @Operation(summary = "핫딜 전체 목록 (삭제 포함)")
    @GetMapping("/api/admin/hot-deals")
    fun getAll(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @PageableDefault(size = 20, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ApiResult<HotDealPageResponse> {
        val result = hotDealAdminUsecase.getAll(pageable)
        val userIds = result.content.map { it.userId }.distinct()
        val nicknames = userQueryPort.findNicknamesByIds(userIds)

        val response = HotDealPageResponse(
            content = result.content.mapIndexed { index, it ->
                val dealNumber = result.totalElements - (result.number.toLong() * result.size) - index
                HotDealResponse.from(it, nicknames[it.userId] ?: "알 수 없음", dealNumber)
            },
            page = result.number,
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
        val deal = hotDealAdminUsecase.update(
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
        val nicknames = userQueryPort.findNicknamesByIds(listOf(deal.userId))
        return ApiResponse.of(ApiResponseCode.OK, HotDealResponse.from(deal, nicknames[deal.userId] ?: "알 수 없음"))
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
