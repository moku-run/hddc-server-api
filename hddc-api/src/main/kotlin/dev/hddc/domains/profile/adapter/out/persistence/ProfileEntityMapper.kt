package dev.hddc.domains.profile.adapter.out.persistence

import dev.hddc.domains.profile.domain.model.ProfileLinkModel
import dev.hddc.domains.profile.domain.model.ProfileModel
import dev.hddc.domains.profile.domain.model.SocialLinkModel

fun ProfileEntity.toDomain(): ProfileModel = ProfileModel(
    id = id,
    userId = userId,
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
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun ProfileEntity.toDomainWithDetails(): ProfileModel = ProfileModel(
    id = id,
    userId = userId,
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
    darkMode = darkMode,
    links = links.filter { !it.isDeleted }.map { it.toDomain() },
    socials = socials.filter { !it.isDeleted }.map { it.toDomain() },
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun ProfileLinkEntity.toDomain(): ProfileLinkModel = ProfileLinkModel(
    id = id,
    profileId = profile?.id,
    title = title,
    url = url,
    imageUrl = imageUrl,
    description = description,
    sortOrder = sortOrder,
    enabled = enabled,
    isDeleted = isDeleted,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun ProfileSocialEntity.toDomain(): SocialLinkModel = SocialLinkModel(
    id = id,
    profileId = profile?.id,
    platform = platform,
    url = url,
    sortOrder = sortOrder,
    isDeleted = isDeleted,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun ProfileModel.toEntity(): ProfileEntity = ProfileEntity(
    userId = userId,
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
).also {
    it.id = id
    it.createdAt = createdAt
    it.updatedAt = updatedAt
}

fun ProfileLinkModel.toEntity(profile: ProfileEntity): ProfileLinkEntity = ProfileLinkEntity(
    title = title,
    url = url,
    imageUrl = imageUrl,
    description = description,
    sortOrder = sortOrder,
    enabled = enabled,
).also {
    it.profile = profile
    it.id = id
    it.createdAt = createdAt
    it.updatedAt = updatedAt
    it.isDeleted = isDeleted
}

fun SocialLinkModel.toEntity(profile: ProfileEntity): ProfileSocialEntity = ProfileSocialEntity(
    platform = platform,
    url = url,
    sortOrder = sortOrder,
).also {
    it.profile = profile
    it.id = id
    it.createdAt = createdAt
    it.updatedAt = updatedAt
    it.isDeleted = isDeleted
}
