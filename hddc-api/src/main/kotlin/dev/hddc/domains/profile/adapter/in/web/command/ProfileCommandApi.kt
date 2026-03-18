package dev.hddc.domains.profile.adapter.`in`.web.command

import dev.hddc.domains.profile.adapter.`in`.web.request.UpdateProfileRequest
import dev.hddc.domains.profile.adapter.`in`.web.response.ProfileResponse
import dev.hddc.domains.profile.application.ports.input.command.ResetProfileUsecase
import dev.hddc.domains.profile.application.ports.input.command.UpdateMyProfileUsecase
import dev.hddc.framework.api.response.ApiResponse
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.security.authentication.UserAuthenticationDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Profile Command", description = "프로필 수정 API")
@RestController
class ProfileCommandApi(
    private val updateMyProfileUsecase: UpdateMyProfileUsecase,
    private val resetProfileUsecase: ResetProfileUsecase,
) {
    @Operation(summary = "내 프로필 수정")
    @PatchMapping("/api/profiles/me")
    fun updateMyProfile(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @Valid @RequestBody request: UpdateProfileRequest,
    ): ResponseEntity<ApiResponse<ProfileResponse>> =
        ApiResponse.of(ApiResponseCode.UPDATED, ProfileResponse.from(updateMyProfileUsecase.execute(user.userId, request.toCommand())))

    @Operation(summary = "프로필 초기화")
    @PostMapping("/api/profiles/me/reset")
    fun resetProfile(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
    ): ResponseEntity<ApiResponse<ProfileResponse>> =
        ApiResponse.of(ApiResponseCode.OK, ProfileResponse.from(resetProfileUsecase.execute(user.userId)))
}
