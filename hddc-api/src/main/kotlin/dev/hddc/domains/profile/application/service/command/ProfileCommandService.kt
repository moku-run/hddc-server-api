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

    override fun execute(userId: Long, command: UpdateProfileCommand): ProfileModel {
        validateFields(command) // CPU 전용 — 트랜잭션 밖에서 수행
        return saveProfile(userId, command)
    }

    @Transactional
    fun saveProfile(userId: Long, command: UpdateProfileCommand): ProfileModel {
        val existing = profileQueryPort.findByUserIdWithDetails(userId)

        if (existing != null && existing.slug != command.slug && profileQueryPort.existsBySlug(command.slug)) {
            throw IllegalArgumentException(ApiResponseCode.PROFILE_SLUG_DUPLICATE.code)
        }
        if (existing == null && profileQueryPort.existsBySlug(command.slug)) {
            throw IllegalArgumentException(ApiResponseCode.PROFILE_SLUG_DUPLICATE.code)
        }

        val profile = existing ?: ProfileModel(userId = userId, slug = command.slug, nickname = command.nickname)

        val updated = profile.copy(
            slug = command.slug,
            nickname = command.nickname,
            bio = command.bio,
            avatarUrl = command.avatarUrl,
            backgroundUrl = command.backgroundUrl,
            backgroundColor = command.backgroundColor,
            backgroundTexture = command.backgroundTexture,
            linkLayout = command.linkLayout,
            linkStyle = command.linkStyle,
            fontFamily = command.fontFamily,
            headerLayout = command.headerLayout,
            linkAnimation = command.linkAnimation,
            colorTheme = command.colorTheme,
            customPrimaryColor = command.customPrimaryColor,
            customSecondaryColor = command.customSecondaryColor,
            fontColor = command.fontColor,
            linkRound = command.linkRound,
            decorator1Type = command.decorator1Type,
            decorator1Text = command.decorator1Text,
            decorator2Type = command.decorator2Type,
            decorator2Text = command.decorator2Text,
            linkGradientFrom = command.linkGradientFrom,
            linkGradientTo = command.linkGradientTo,
            linkBorderColor = command.linkBorderColor,
            linkBorderThick = command.linkBorderThick,
            pageLayout = command.pageLayout,
            darkMode = command.darkMode,
            updatedAt = Instant.now(),
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
                    price = linkCmd.price,
                    originalPrice = linkCmd.originalPrice,
                    discountRate = linkCmd.discountRate,
                    store = linkCmd.store,
                    category = linkCmd.category,
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

    private fun validateFields(command: UpdateProfileCommand) {
        ProfileFieldSpec.validateProfileFields(
            colorTheme = command.colorTheme,
            fontFamily = command.fontFamily,
            linkLayout = command.linkLayout,
            linkStyle = command.linkStyle,
            headerLayout = command.headerLayout,
            linkAnimation = command.linkAnimation,
            linkRound = command.linkRound,
            pageLayout = command.pageLayout,
            linkBorderThick = command.linkBorderThick,
            backgroundTexture = command.backgroundTexture,
        )?.let {
            throw IllegalArgumentException(ApiResponseCode.PROFILE_INVALID_FIELD.code)
        }

        command.socials.forEach { socialCmd ->
            require(ProfileFieldSpec.validateSocialPlatform(socialCmd.platform)) {
                ApiResponseCode.PROFILE_INVALID_FIELD.code
            }
        }
    }
}
