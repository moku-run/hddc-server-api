package dev.hddc.domains.profile.adapter.out.persistence

import dev.hddc.domains.profile.application.ports.output.command.ProfileLinkCommandPort
import dev.hddc.domains.profile.domain.model.ProfileLinkModel
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class ProfileLinkPersistenceAdapter(
    private val profileLinkRepository: ProfileLinkRepository,
    private val profileRepository: ProfileRepository,
) : ProfileLinkCommandPort {

    override fun findById(linkId: Long): ProfileLinkModel? =
        profileLinkRepository.findById(linkId).orElse(null)?.toDomain()

    override fun save(model: ProfileLinkModel): ProfileLinkModel {
        val entity = if (model.id != null) {
            val existing = profileLinkRepository.findById(model.id).orElse(null)
            if (existing != null) {
                existing.apply {
                    title = model.title
                    url = model.url
                    imageUrl = model.imageUrl
                    description = model.description
                    sortOrder = model.sortOrder
                    enabled = model.enabled
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
        return profileLinkRepository.save(entity).toDomain()
    }

    override fun saveAll(models: List<ProfileLinkModel>): List<ProfileLinkModel> =
        models.map { save(it) }

    override fun countByProfileId(profileId: Long): Long =
        profileLinkRepository.countByProfileIdAndIsDeletedFalse(profileId)

    override fun findAllByProfileId(profileId: Long): List<ProfileLinkModel> =
        profileLinkRepository.findAllByProfileIdAndIsDeletedFalseOrderBySortOrderAsc(profileId)
            .map { it.toDomain() }

    private fun createNewEntity(model: ProfileLinkModel): ProfileLinkEntity {
        val profile = profileRepository.loadById(model.profileId!!)
        return ProfileLinkEntity(
            title = model.title,
            url = model.url,
            imageUrl = model.imageUrl,
            description = model.description,
            sortOrder = model.sortOrder,
            enabled = model.enabled,
        ).also { it.profile = profile }
    }
}
