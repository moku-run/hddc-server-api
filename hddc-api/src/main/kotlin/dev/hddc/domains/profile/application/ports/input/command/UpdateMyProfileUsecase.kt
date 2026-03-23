package dev.hddc.domains.profile.application.ports.input.command

import dev.hddc.domains.profile.domain.model.ProfileModel

data class UpdateProfileCommand(
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
    val links: List<ProfileLinkCommand>,
    val socials: List<SocialLinkCommand>,
)

data class ProfileLinkCommand(
    val id: Long?,
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
)

data class SocialLinkCommand(
    val id: Long?,
    val platform: String,
    val url: String,
)

interface UpdateMyProfileUsecase {
    fun execute(userId: Long, command: UpdateProfileCommand): ProfileModel
}
