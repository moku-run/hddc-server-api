package dev.hddc.domains.hotdeal.adapter.`in`.web.command

import dev.hddc.domains.hotdeal.application.ports.input.command.DealClickSyncUsecase
import dev.hddc.domains.hotdeal.application.ports.input.command.DealClickUsecase
import dev.hddc.framework.api.response.ApiResponse
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.api.response.ApiResult
import dev.hddc.framework.security.authentication.UserAuthenticationDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Hot Deal Click", description = "핫딜 클릭 API")
@RestController
class DealClickApi(
    private val dealClickUsecase: DealClickUsecase,
    private val dealClickSyncUsecase: DealClickSyncUsecase,
) {
    @Operation(summary = "회원 클릭 기록")
    @PostMapping("/api/hot-deals/{dealId}/clicks")
    fun click(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @PathVariable dealId: Long,
        request: HttpServletRequest,
    ): ApiResult<Nothing> {
        val ip = request.getHeader("X-Forwarded-For")?.split(",")?.first()?.trim()
            ?: request.remoteAddr
        dealClickUsecase.click(dealId, user.userId, ip)
        return ApiResponse.of(ApiResponseCode.OK)
    }

    @Operation(summary = "로그인 시 localStorage 클릭 일괄 동기화")
    @PostMapping("/api/hot-deals/clicks/sync")
    fun sync(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @RequestBody request: ClickSyncRequest,
    ): ApiResult<ClickSyncResponse> {
        val synced = dealClickSyncUsecase.sync(user.userId, request.dealIds)
        return ApiResponse.of(ApiResponseCode.OK, ClickSyncResponse(synced))
    }
}

data class ClickSyncRequest(
    val dealIds: List<Long>,
)

data class ClickSyncResponse(
    val syncedCount: Int,
)
