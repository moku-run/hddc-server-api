package dev.hddc.domains.profile.application.ports.input.command

import dev.hddc.domains.profile.domain.model.ProfileModel

data class UpdateProfileCommand(
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
    val fontColor: String?,
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
)

data class SocialLinkCommand(
    val id: Long?,
    val platform: String,
    val url: String,
)

interface UpdateMyProfileUsecase {
    fun execute(userId: Long, command: UpdateProfileCommand): ProfileModel
}
