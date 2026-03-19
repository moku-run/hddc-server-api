package dev.hddc.domains.profile.domain.model

import java.time.Instant

data class ProfileModel(
    val id: Long? = null,
    val userId: Long,
    val slug: String,
    val nickname: String,
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
    val links: List<ProfileLinkModel> = emptyList(),
    val socials: List<SocialLinkModel> = emptyList(),
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
)
