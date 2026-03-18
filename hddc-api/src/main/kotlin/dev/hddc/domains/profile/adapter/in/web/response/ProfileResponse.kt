package dev.hddc.domains.profile.adapter.`in`.web.response

import dev.hddc.domains.profile.domain.model.ProfileLinkModel
import dev.hddc.domains.profile.domain.model.ProfileModel
import dev.hddc.domains.profile.domain.model.SocialLinkModel

data class ProfileResponse(
    val slug: String,
    val nickname: String,
    val bio: String?,
    val avatarUrl: String?,
    val backgroundUrl: String?,
    val backgroundColor: String?,
    val linkLayout: String,
    val linkStyle: String,
    val fontFamily: String,
    val headerLayout: String,
    val linkAnimation: String,
    val colorTheme: String,
    val customPrimaryColor: String?,
    val customSecondaryColor: String?,
    val darkMode: Boolean,
    val links: List<ProfileLinkResponse>,
    val socials: List<SocialLinkResponse>,
    val createdAt: String,
    val updatedAt: String,
) {
    companion object {
        fun from(model: ProfileModel): ProfileResponse = ProfileResponse(
            slug = model.slug,
            nickname = model.nickname,
            bio = model.bio,
            avatarUrl = model.avatarUrl,
            backgroundUrl = model.backgroundUrl,
            backgroundColor = model.backgroundColor,
            linkLayout = model.linkLayout,
            linkStyle = model.linkStyle,
            fontFamily = model.fontFamily,
            headerLayout = model.headerLayout,
            linkAnimation = model.linkAnimation,
            colorTheme = model.colorTheme,
            customPrimaryColor = model.customPrimaryColor,
            customSecondaryColor = model.customSecondaryColor,
            darkMode = model.darkMode,
            links = model.links.filter { !it.isDeleted }.map { ProfileLinkResponse.from(it) },
            socials = model.socials.filter { !it.isDeleted }.map { SocialLinkResponse.from(it) },
            createdAt = model.createdAt.toString(),
            updatedAt = model.updatedAt.toString(),
        )
    }
}

data class ProfileLinkResponse(
    val id: Long,
    val title: String,
    val url: String,
    val imageUrl: String?,
    val description: String?,
    val order: Int,
    val enabled: Boolean,
) {
    companion object {
        fun from(model: ProfileLinkModel): ProfileLinkResponse = ProfileLinkResponse(
            id = model.id!!,
            title = model.title,
            url = model.url,
            imageUrl = model.imageUrl,
            description = model.description,
            order = model.sortOrder,
            enabled = model.enabled,
        )
    }
}

data class SocialLinkResponse(
    val id: Long,
    val platform: String,
    val url: String,
) {
    companion object {
        fun from(model: SocialLinkModel): SocialLinkResponse = SocialLinkResponse(
            id = model.id!!,
            platform = model.platform,
            url = model.url,
        )
    }
}
