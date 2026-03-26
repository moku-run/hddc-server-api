package dev.hddc.domains.profile.adapter.`in`.web.command

import dev.hddc.domains.profile.adapter.`in`.web.command.request.AddSocialRequest
import dev.hddc.domains.profile.adapter.`in`.web.command.request.ReorderRequest
import dev.hddc.domains.profile.adapter.`in`.web.command.request.UpdateSocialRequest
import dev.hddc.domains.profile.adapter.`in`.web.response.SocialLinkResponse
import dev.hddc.domains.profile.application.ports.input.command.SocialCommandUsecase
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

@Tag(name = "Profile Social Command", description = "프로필 소셜 링크 CRUD API")
@RestController
class SocialCommandApi(
    private val socialCommandUsecase: SocialCommandUsecase,
) {
    @Operation(summary = "소셜 링크 추가")
    @PostMapping("/api/profiles/me/socials")
    fun addSocial(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @Valid @RequestBody request: AddSocialRequest,
    ): ApiResult<SocialLinkResponse> =
        ApiResponse.of(ApiResponseCode.CREATED, SocialLinkResponse.from(socialCommandUsecase.addSocial(user.userId, request.toCommand())))

    @Operation(summary = "소셜 링크 수정")
    @PatchMapping("/api/profiles/me/socials/{socialId}")
    fun updateSocial(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @PathVariable socialId: Long,
        @Valid @RequestBody request: UpdateSocialRequest,
    ): ApiResult<SocialLinkResponse> =
        ApiResponse.of(ApiResponseCode.UPDATED, SocialLinkResponse.from(socialCommandUsecase.updateSocial(user.userId, socialId, request.toCommand())))

    @Operation(summary = "소셜 링크 삭제")
    @DeleteMapping("/api/profiles/me/socials/{socialId}")
    fun deleteSocial(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @PathVariable socialId: Long,
    ): ApiResult<Nothing> {
        socialCommandUsecase.deleteSocial(user.userId, socialId)
        return ApiResponse.of(ApiResponseCode.DELETED)
    }

    @Operation(summary = "소셜 링크 순서 변경")
    @PutMapping("/api/profiles/me/socials/order")
    fun reorderSocials(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @Valid @RequestBody request: ReorderRequest,
    ): ApiResult<List<SocialLinkResponse>> =
        ApiResponse.of(ApiResponseCode.UPDATED, socialCommandUsecase.reorderSocials(user.userId, request.orderedIds).map { SocialLinkResponse.from(it) })
}
