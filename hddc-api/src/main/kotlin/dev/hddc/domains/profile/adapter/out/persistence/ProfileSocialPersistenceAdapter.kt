package dev.hddc.domains.profile.adapter.out.persistence

import dev.hddc.domains.profile.application.ports.output.command.ProfileSocialCommandPort
import dev.hddc.domains.profile.domain.model.SocialLinkModel
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class ProfileSocialPersistenceAdapter(
    private val profileSocialRepository: ProfileSocialRepository,
    private val profileRepository: ProfileRepository,
) : ProfileSocialCommandPort {

    override fun findById(socialId: Long): SocialLinkModel? =
        profileSocialRepository.findById(socialId).orElse(null)?.toDomain()

    override fun save(model: SocialLinkModel): SocialLinkModel {
        val entity = if (model.id != null) {
            val existing = profileSocialRepository.findById(model.id).orElse(null)
            if (existing != null) {
                existing.apply {
                    platform = model.platform
                    url = model.url
                    sortOrder = model.sortOrder
                    isDeleted = model.isDeleted
                    deletedAt = if (model.isDeleted && deletedAt == null) Instant.now() else deletedAt
                    updatedAt = Instant.now()
                }
                existing
            } else {
                createNewEntity(model)
            }
        } else {
            createNewEntity(model)
        }
        return profileSocialRepository.save(entity).toDomain()
    }

    override fun saveAll(models: List<SocialLinkModel>): List<SocialLinkModel> =
        models.map { save(it) }

    override fun countByProfileId(profileId: Long): Long =
        profileSocialRepository.countByProfileIdAndIsDeletedFalse(profileId)

    override fun findAllByProfileId(profileId: Long): List<SocialLinkModel> =
        profileSocialRepository.findAllByProfileIdAndIsDeletedFalseOrderBySortOrderAsc(profileId)
            .map { it.toDomain() }

    override fun existsByProfileIdAndPlatform(profileId: Long, platform: String): Boolean =
        profileSocialRepository.existsByProfileIdAndPlatformAndIsDeletedFalse(profileId, platform)

    private fun createNewEntity(model: SocialLinkModel): ProfileSocialEntity {
        val profile = profileRepository.findById(model.profileId!!).orElseThrow()
        return ProfileSocialEntity(
            platform = model.platform,
            url = model.url,
            sortOrder = model.sortOrder,
        ).also { it.profile = profile }
    }
}
