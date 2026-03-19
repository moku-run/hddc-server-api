package dev.hddc.domains.profile.adapter.`in`.web.request

import dev.hddc.domains.profile.application.ports.input.command.ProfileLinkCommand
import dev.hddc.domains.profile.application.ports.input.command.SocialLinkCommand
import dev.hddc.domains.profile.application.ports.input.command.UpdateProfileCommand
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UpdateProfileRequest(
    @field:NotBlank(message = "slug는 필수입니다.")
    @field:Size(min = 3, max = 30, message = "slug는 3~30자여야 합니다.")
    @field:Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "slug는 영문, 숫자, 하이픈, 언더바만 사용 가능합니다.")
    val slug: String,

    @field:NotBlank(message = "닉네임은 필수입니다.")
    @field:Size(min = 2, max = 20, message = "닉네임은 2~20자여야 합니다.")
    val nickname: String,

    @field:Size(max = 80, message = "소개는 80자 이하여야 합니다.")
    val bio: String? = null,

    val avatarUrl: String? = null,
    val backgroundUrl: String? = null,
    val backgroundColor: String? = null,
    val linkLayout: String = "list",
    val linkStyle: String = "fill",
    val fontFamily: String = "pretendard",
    val headerLayout: String = "center",
    val linkAnimation: String = "none",
    val colorTheme: String = "default",
    val customPrimaryColor: String? = null,
    val customSecondaryColor: String? = null,
    val fontColor: String? = null,
    val darkMode: Boolean = false,

    @field:Valid
    @field:Size(max = 20, message = "링크는 최대 20개까지 가능합니다.")
    val links: List<ProfileLinkRequest> = emptyList(),

    @field:Valid
    @field:Size(max = 8, message = "소셜 링크는 최대 8개까지 가능합니다.")
    val socials: List<SocialLinkRequest> = emptyList(),
) {
    fun toCommand(): UpdateProfileCommand = UpdateProfileCommand(
        slug = slug,
        nickname = nickname,
        bio = bio,
        avatarUrl = avatarUrl,
        backgroundUrl = backgroundUrl,
        backgroundColor = backgroundColor,
        linkLayout = linkLayout,
        linkStyle = linkStyle,
        fontFamily = fontFamily,
        headerLayout = headerLayout,
        linkAnimation = linkAnimation,
        colorTheme = colorTheme,
        customPrimaryColor = customPrimaryColor,
        customSecondaryColor = customSecondaryColor,
        fontColor = fontColor,
        darkMode = darkMode,
        links = links.map { it.toCommand() },
        socials = socials.map { it.toCommand() },
    )
}

data class ProfileLinkRequest(
    val id: Long? = null,

    @field:NotBlank(message = "링크 제목은 필수입니다.")
    @field:Size(max = 100, message = "링크 제목은 100자 이하여야 합니다.")
    val title: String,

    @field:NotBlank(message = "링크 URL은 필수입니다.")
    @field:Size(max = 500, message = "링크 URL은 500자 이하여야 합니다.")
    val url: String,

    val imageUrl: String? = null,

    @field:Size(max = 200, message = "설명은 200자 이하여야 합니다.")
    val description: String? = null,

    val order: Int = 0,
    val enabled: Boolean = true,
) {
    fun toCommand(): ProfileLinkCommand = ProfileLinkCommand(
        id = id,
        title = title,
        url = url,
        imageUrl = imageUrl,
        description = description,
        order = order,
        enabled = enabled,
    )
}

data class SocialLinkRequest(
    val id: Long? = null,

    @field:NotBlank(message = "플랫폼은 필수입니다.")
    val platform: String,

    @field:NotBlank(message = "소셜 URL은 필수입니다.")
    @field:Size(max = 500, message = "소셜 URL은 500자 이하여야 합니다.")
    val url: String,
) {
    fun toCommand(): SocialLinkCommand = SocialLinkCommand(
        id = id,
        platform = platform,
        url = url,
    )
}
