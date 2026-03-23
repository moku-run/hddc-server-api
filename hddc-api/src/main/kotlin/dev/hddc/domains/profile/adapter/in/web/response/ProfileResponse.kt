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
    val backgroundTexture: String?,
    val linkLayout: String,
    val linkStyle: String,
    val fontFamily: String,
    val headerLayout: String,
    val linkAnimation: String,
    val colorTheme: String,
    val customPrimaryColor: String?,
    val customSecondaryColor: String?,
    val fontColor: String?,
    val linkRound: String,
    val decorator1Type: String?,
    val decorator1Text: String?,
    val decorator2Type: String?,
    val decorator2Text: String?,
    val linkGradientFrom: String?,
    val linkGradientTo: String?,
    val linkBorderColor: String?,
    val linkBorderThick: String,
    val pageLayout: String,
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
            backgroundTexture = model.backgroundTexture,
            linkLayout = model.linkLayout,
            linkStyle = model.linkStyle,
            fontFamily = model.fontFamily,
            headerLayout = model.headerLayout,
            linkAnimation = model.linkAnimation,
            colorTheme = model.colorTheme,
            customPrimaryColor = model.customPrimaryColor,
            customSecondaryColor = model.customSecondaryColor,
            fontColor = model.fontColor,
            linkRound = model.linkRound,
            decorator1Type = model.decorator1Type,
            decorator1Text = model.decorator1Text,
            decorator2Type = model.decorator2Type,
            decorator2Text = model.decorator2Text,
            linkGradientFrom = model.linkGradientFrom,
            linkGradientTo = model.linkGradientTo,
            linkBorderColor = model.linkBorderColor,
            linkBorderThick = model.linkBorderThick,
            pageLayout = model.pageLayout,
            darkMode = model.darkMode,
            links = model.links.map { ProfileLinkResponse.from(it) },
            socials = model.socials.map { SocialLinkResponse.from(it) },
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
    val price: Long?,
    val originalPrice: Long?,
    val discountRate: Int?,
    val store: String?,
    val category: String?,
    val clicks: Long,
    val likes: Long,
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
            price = model.price,
            originalPrice = model.originalPrice,
            discountRate = model.discountRate,
            store = model.store,
            category = model.category,
            clicks = model.clicks,
            likes = model.likes,
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
