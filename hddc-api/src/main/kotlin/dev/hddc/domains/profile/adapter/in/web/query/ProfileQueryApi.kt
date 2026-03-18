package dev.hddc.domains.profile.adapter.`in`.web.query

import dev.hddc.domains.profile.adapter.`in`.web.response.ProfileResponse
import dev.hddc.domains.profile.application.ports.input.query.GetMyProfileUsecase
import dev.hddc.domains.profile.application.ports.input.query.GetPublicProfileUsecase
import dev.hddc.domains.profile.application.ports.input.query.ValidateSlugResult
import dev.hddc.domains.profile.application.ports.input.query.ValidateSlugUsecase
import dev.hddc.domains.profile.application.ports.output.query.ProfileQueryPort
import dev.hddc.framework.api.response.ApiResponse
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.security.authentication.UserAuthenticationDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Profile Query", description = "프로필 조회 API")
@RestController
class ProfileQueryApi(
    private val getMyProfileUsecase: GetMyProfileUsecase,
    private val getPublicProfileUsecase: GetPublicProfileUsecase,
    private val validateSlugUsecase: ValidateSlugUsecase,
    private val profileQueryPort: ProfileQueryPort,
) {
    @Operation(summary = "내 프로필 조회")
    @GetMapping("/api/profiles/me")
    fun getMyProfile(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
    ): ResponseEntity<ApiResponse<ProfileResponse>> =
        ApiResponse.of(ApiResponseCode.OK, ProfileResponse.from(getMyProfileUsecase.execute(user.userId)))

    @Operation(summary = "공개 프로필 조회 (slug)")
    @GetMapping("/api/profiles/{slug}")
    fun getPublicProfile(
        @PathVariable slug: String,
    ): ResponseEntity<ApiResponse<ProfileResponse>> =
        ApiResponse.of(ApiResponseCode.OK, ProfileResponse.from(getPublicProfileUsecase.execute(slug)))

    @Operation(summary = "슬러그 중복 확인")
    @GetMapping("/api/profiles/check-slug")
    fun checkSlug(
        @AuthenticationPrincipal user: UserAuthenticationDTO,
        @RequestParam slug: String,
    ): ResponseEntity<ApiResponse<ValidateSlugResult>> =
        ApiResponse.of(ApiResponseCode.OK, validateSlugUsecase.execute(user.userId, slug))

    @Operation(summary = "피드에 노출할 추천 프로필")
    @GetMapping("/api/profiles/curated")
    fun getCuratedProfiles(): ResponseEntity<ApiResponse<List<CuratedProfileResponse>>> {
        val profiles = profileQueryPort.findCuratedProfiles(6)
        val result = profiles.map {
            CuratedProfileResponse(
                slug = it.slug,
                nickname = it.nickname,
                bio = it.bio,
                avatarUrl = it.avatarUrl,
            )
        }
        return ApiResponse.of(ApiResponseCode.OK, result)
    }
}

data class CuratedProfileResponse(
    val slug: String,
    val nickname: String,
    val bio: String?,
    val avatarUrl: String?,
)
