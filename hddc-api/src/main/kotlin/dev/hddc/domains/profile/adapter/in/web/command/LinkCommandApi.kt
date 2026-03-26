package dev.hddc.domains.profile.adapter.`in`.web.command

import dev.hddc.domains.profile.adapter.`in`.web.command.request.AddLinkRequest
import dev.hddc.domains.profile.adapter.`in`.web.command.request.ReorderRequest
import dev.hddc.domains.profile.adapter.`in`.web.command.request.UpdateLinkRequest
import dev.hddc.domains.profile.adapter.`in`.web.response.ProfileLinkResponse
import dev.hddc.domains.profile.application.ports.input.command.LinkCommandUsecase
import dev.hddc.domains.profile.application.ports.input.command.ToggleLinkResult
import dev.hddc.framework.api.response.ApiResponse
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.api.response.ApiResult
import dev.hddc.framework.security.authentication.UserAuthenticationDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Profile Link Command", description = "프로필 링크 CRUD API")
@RestController
class LinkCommandApi(
    private val linkCommandUsecase: LinkCommandUsecase,
) {
    @Operation(summary = "링크 추가")
    @PostMapping("/api/profiles/me/links")
    fun addLink(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @Valid @RequestBody request: AddLinkRequest,
    ): ApiResult<ProfileLinkResponse> =
        ApiResponse.of(ApiResponseCode.CREATED, ProfileLinkResponse.from(linkCommandUsecase.addLink(user.userId, request.toCommand())))

    @Operation(summary = "링크 수정")
    @PatchMapping("/api/profiles/me/links/{linkId}")
    fun updateLink(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @PathVariable linkId: Long,
        @Valid @RequestBody request: UpdateLinkRequest,
    ): ApiResult<ProfileLinkResponse> =
        ApiResponse.of(ApiResponseCode.UPDATED, ProfileLinkResponse.from(linkCommandUsecase.updateLink(user.userId, linkId, request.toCommand())))

    @Operation(summary = "링크 삭제")
    @DeleteMapping("/api/profiles/me/links/{linkId}")
    fun deleteLink(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @PathVariable linkId: Long,
    ): ApiResult<Nothing> {
        linkCommandUsecase.deleteLink(user.userId, linkId)
        return ApiResponse.of(ApiResponseCode.DELETED)
    }

    @Operation(summary = "링크 활성/비활성 토글")
    @PatchMapping("/api/profiles/me/links/{linkId}/toggle")
    fun toggleLink(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @PathVariable linkId: Long,
    ): ApiResult<ToggleLinkResult> =
        ApiResponse.of(ApiResponseCode.UPDATED, linkCommandUsecase.toggleLink(user.userId, linkId))

    @Operation(summary = "링크 순서 변경")
    @PutMapping("/api/profiles/me/links/order")
    fun reorderLinks(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @Valid @RequestBody request: ReorderRequest,
    ): ApiResult<List<ProfileLinkResponse>> =
        ApiResponse.of(ApiResponseCode.UPDATED, linkCommandUsecase.reorderLinks(user.userId, request.orderedIds).map { ProfileLinkResponse.from(it) })
}
