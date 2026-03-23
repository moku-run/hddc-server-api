package dev.hddc.domains.profile.adapter.out.persistence

import dev.hddc.domains.profile.application.ports.output.command.ProfileCommandPort
import dev.hddc.domains.profile.application.ports.output.query.ProfileQueryPort
import dev.hddc.domains.profile.domain.model.ProfileModel
import org.springframework.stereotype.Repository

@Repository
class ProfilePersistenceAdapter(
    private val profileRepository: ProfileRepository,
) : ProfileQueryPort, ProfileCommandPort {

    override fun findByUserId(userId: Long): ProfileModel? =
        profileRepository.findByUserId(userId)?.toDomain()

    override fun findByUserIdWithDetails(userId: Long): ProfileModel? =
        profileRepository.findByUserIdWithLinksAndSocials(userId)?.toDomainWithDetails()

    override fun findBySlugWithDetails(slug: String): ProfileModel? =
        profileRepository.findBySlugWithLinksAndSocials(slug)?.toDomainWithDetails()

    override fun existsBySlug(slug: String): Boolean =
        profileRepository.existsBySlug(slug)

    override fun findCuratedProfiles(limit: Int): List<ProfileModel> =
        profileRepository.findCuratedProfiles(limit).map { it.toDomain() }

    override fun save(model: ProfileModel): ProfileModel {
        val entity = if (model.id != null) {
            val existing = profileRepository.findById(model.id).orElse(null)
            if (existing != null) {
                existing.apply {
                    slug = model.slug
                    nickname = model.nickname
                    bio = model.bio
                    avatarUrl = model.avatarUrl
                    backgroundUrl = model.backgroundUrl
                    backgroundColor = model.backgroundColor
                    backgroundTexture = model.backgroundTexture
                    linkLayout = model.linkLayout
                    linkStyle = model.linkStyle
                    fontFamily = model.fontFamily
                    headerLayout = model.headerLayout
                    linkAnimation = model.linkAnimation
                    colorTheme = model.colorTheme
                    customPrimaryColor = model.customPrimaryColor
                    customSecondaryColor = model.customSecondaryColor
                    fontColor = model.fontColor
                    linkRound = model.linkRound
                    decorator1Type = model.decorator1Type
                    decorator1Text = model.decorator1Text
                    decorator2Type = model.decorator2Type
                    decorator2Text = model.decorator2Text
                    linkGradientFrom = model.linkGradientFrom
                    linkGradientTo = model.linkGradientTo
                    linkBorderColor = model.linkBorderColor
                    linkBorderThick = model.linkBorderThick
                    pageLayout = model.pageLayout
                    darkMode = model.darkMode
                }

                syncLinks(existing, model)
                syncSocials(existing, model)

                existing
            } else {
                model.toEntity()
            }
        } else {
            model.toEntity()
        }

        return profileRepository.save(entity).toDomain()
    }

    private fun syncLinks(existing: ProfileEntity, model: ProfileModel) {
        val incomingById = model.links.filter { it.id != null }.associateBy { it.id }
        val existingById = existing.links.associateBy { it.id }

        existing.links.removeIf { it.id !in incomingById && it.id != null }

        incomingById.forEach { (id, linkModel) ->
            val entity = existingById[id]
            if (entity != null) {
                entity.title = linkModel.title
                entity.url = linkModel.url
                entity.imageUrl = linkModel.imageUrl
                entity.description = linkModel.description
                entity.sortOrder = linkModel.sortOrder
                entity.enabled = linkModel.enabled
                entity.price = linkModel.price
                entity.originalPrice = linkModel.originalPrice
                entity.discountRate = linkModel.discountRate
                entity.store = linkModel.store
                entity.category = linkModel.category
                entity.isDeleted = linkModel.isDeleted
            } else {
                existing.links.add(linkModel.toEntity(existing))
            }
        }

        model.links.filter { it.id == null }.forEach { linkModel ->
            existing.links.add(linkModel.toEntity(existing))
        }
    }

    private fun syncSocials(existing: ProfileEntity, model: ProfileModel) {
        val incomingById = model.socials.filter { it.id != null }.associateBy { it.id }
        val existingById = existing.socials.associateBy { it.id }

        existing.socials.removeIf { it.id !in incomingById && it.id != null }

        incomingById.forEach { (id, socialModel) ->
            val entity = existingById[id]
            if (entity != null) {
                entity.platform = socialModel.platform
                entity.url = socialModel.url
                entity.sortOrder = socialModel.sortOrder
                entity.isDeleted = socialModel.isDeleted
            } else {
                existing.socials.add(socialModel.toEntity(existing))
            }
        }

        model.socials.filter { it.id == null }.forEach { socialModel ->
            existing.socials.add(socialModel.toEntity(existing))
        }
    }
}
