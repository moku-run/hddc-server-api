package dev.hddc.domains.profile.application.service.command

import dev.hddc.domains.profile.application.ports.input.command.AddSocialCommand
import dev.hddc.domains.profile.application.ports.input.command.SocialCommandUsecase
import dev.hddc.domains.profile.application.ports.input.command.UpdateSocialCommand
import dev.hddc.domains.profile.application.ports.output.command.ProfileSocialCommandPort
import dev.hddc.domains.profile.application.ports.output.query.ProfileQueryPort
import dev.hddc.domains.profile.domain.model.SocialLinkModel
import dev.hddc.framework.api.response.ApiResponseCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class SocialCommandService(
    private val profileQueryPort: ProfileQueryPort,
    private val profileSocialCommandPort: ProfileSocialCommandPort,
) : SocialCommandUsecase {

    @Transactional
    override fun addSocial(userId: Long, command: AddSocialCommand): SocialLinkModel {
        val profile = profileQueryPort.findByUserId(userId)
            ?: throw IllegalArgumentException(ApiResponseCode.PROFILE_NOT_FOUND.code)

        val count = profileSocialCommandPort.countByProfileId(profile.id!!)
        require(count < 8) { ApiResponseCode.PROFILE_SOCIAL_LIMIT_EXCEEDED.code }

        require(!profileSocialCommandPort.existsByProfileIdAndPlatform(profile.id!!, command.platform)) {
            ApiResponseCode.PROFILE_SOCIAL_DUPLICATE_PLATFORM.code
        }

        val model = SocialLinkModel(
            profileId = profile.id,
            platform = command.platform,
            url = command.url,
            sortOrder = count.toInt(),
        )
        return profileSocialCommandPort.save(model)
    }

    @Transactional
    override fun updateSocial(userId: Long, socialId: Long, command: UpdateSocialCommand): SocialLinkModel {
        val social = findOwnedSocial(userId, socialId)
        return profileSocialCommandPort.save(social.copy(url = command.url, updatedAt = Instant.now()))
    }

    @Transactional
    override fun deleteSocial(userId: Long, socialId: Long) {
        val social = findOwnedSocial(userId, socialId)
        profileSocialCommandPort.save(social.copy(isDeleted = true))
    }

    @Transactional
    override fun reorderSocials(userId: Long, orderedIds: List<Long>): List<SocialLinkModel> {
        val profile = profileQueryPort.findByUserId(userId)
            ?: throw IllegalArgumentException(ApiResponseCode.PROFILE_NOT_FOUND.code)
        val socials = profileSocialCommandPort.findAllByProfileId(profile.id!!)
        val reordered = socials.map { social ->
            val newOrder = orderedIds.indexOf(social.id)
            if (newOrder >= 0) social.copy(sortOrder = newOrder, updatedAt = Instant.now()) else social
        }
        return profileSocialCommandPort.saveAll(reordered).sortedBy { it.sortOrder }
    }

    private fun findOwnedSocial(userId: Long, socialId: Long): SocialLinkModel {
        val profile = profileQueryPort.findByUserId(userId)
            ?: throw IllegalArgumentException(ApiResponseCode.PROFILE_NOT_FOUND.code)
        val social = profileSocialCommandPort.findById(socialId)
            ?: throw IllegalArgumentException(ApiResponseCode.PROFILE_SOCIAL_NOT_FOUND.code)
        require(social.profileId == profile.id && !social.isDeleted) {
            ApiResponseCode.PROFILE_SOCIAL_NOT_FOUND.code
        }
        return social
    }
}
