package dev.hddc.domains.profile.application.service.command

import dev.hddc.domains.profile.application.ports.input.command.UpdateMyProfileUsecase
import dev.hddc.domains.profile.application.ports.input.command.UpdateProfileCommand
import dev.hddc.domains.profile.application.ports.output.command.ProfileCommandPort
import dev.hddc.domains.profile.application.ports.output.query.ProfileQueryPort
import dev.hddc.domains.profile.domain.model.ProfileLinkModel
import dev.hddc.domains.profile.domain.model.ProfileModel
import dev.hddc.domains.profile.domain.model.SocialLinkModel
import dev.hddc.domains.profile.domain.spec.ProfileFieldSpec
import dev.hddc.framework.api.response.ApiResponseCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class ProfileCommandService(
    private val profileQueryPort: ProfileQueryPort,
    private val profileCommandPort: ProfileCommandPort,
) : UpdateMyProfileUsecase {

    @Transactional
    override fun execute(userId: Long, command: UpdateProfileCommand): ProfileModel {
        val profile = profileQueryPort.findByUserIdWithDetails(userId)
            ?: throw IllegalArgumentException(ApiResponseCode.PROFILE_NOT_FOUND.code)

        if (profile.slug != command.slug && profileQueryPort.existsBySlug(command.slug)) {
            throw IllegalArgumentException(ApiResponseCode.PROFILE_SLUG_DUPLICATE.code)
        }

        ProfileFieldSpec.validateProfileFields(
            colorTheme = command.colorTheme,
            fontFamily = command.fontFamily,
            linkLayout = command.linkLayout,
            linkStyle = command.linkStyle,
            headerLayout = command.headerLayout,
            linkAnimation = command.linkAnimation,
        )?.let { invalidField ->
            throw IllegalArgumentException(ApiResponseCode.PROFILE_INVALID_FIELD.code)
        }

        command.socials.forEach { socialCmd ->
            require(ProfileFieldSpec.validateSocialPlatform(socialCmd.platform)) {
                ApiResponseCode.PROFILE_INVALID_FIELD.code
            }
        }

        val now = Instant.now()
        val updated = profile.copy(
            slug = command.slug,
            nickname = command.nickname,
            bio = command.bio,
            avatarUrl = command.avatarUrl,
            backgroundUrl = command.backgroundUrl,
            backgroundColor = command.backgroundColor,
            linkLayout = command.linkLayout,
            linkStyle = command.linkStyle,
            fontFamily = command.fontFamily,
            headerLayout = command.headerLayout,
            linkAnimation = command.linkAnimation,
            colorTheme = command.colorTheme,
            customPrimaryColor = command.customPrimaryColor,
            customSecondaryColor = command.customSecondaryColor,
            fontColor = command.fontColor,
            darkMode = command.darkMode,
            updatedAt = now,
            links = command.links.map { linkCmd ->
                ProfileLinkModel(
                    id = linkCmd.id,
                    profileId = profile.id,
                    title = linkCmd.title,
                    url = linkCmd.url,
                    imageUrl = linkCmd.imageUrl,
                    description = linkCmd.description,
                    sortOrder = linkCmd.order,
                    enabled = linkCmd.enabled,
                )
            },
            socials = command.socials.map { socialCmd ->
                SocialLinkModel(
                    id = socialCmd.id,
                    profileId = profile.id,
                    platform = socialCmd.platform,
                    url = socialCmd.url,
                )
            },
        )

        return profileCommandPort.save(updated)
    }
}
