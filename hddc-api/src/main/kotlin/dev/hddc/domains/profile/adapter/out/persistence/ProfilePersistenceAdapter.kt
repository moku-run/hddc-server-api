package dev.hddc.domains.profile.adapter.out.persistence

import dev.hddc.domains.profile.application.ports.output.command.ProfileCommandPort
import dev.hddc.domains.profile.application.ports.output.query.ProfileQueryPort
import dev.hddc.domains.profile.domain.model.ProfileModel
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class ProfilePersistenceAdapter(
    private val profileRepository: ProfileRepository,
) : ProfileQueryPort, ProfileCommandPort {

    override fun findByUserId(userId: Long): ProfileModel? =
        profileRepository.findByUserId(userId)?.toDomain()

    override fun findByUserIdWithDetails(userId: Long): ProfileModel? =
        profileRepository.findByUserIdWithLinksAndSocials(userId)?.toDomain()

    override fun findBySlugWithDetails(slug: String): ProfileModel? =
        profileRepository.findBySlugWithLinksAndSocials(slug)?.toDomain()

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
                    linkLayout = model.linkLayout
                    linkStyle = model.linkStyle
                    fontFamily = model.fontFamily
                    headerLayout = model.headerLayout
                    linkAnimation = model.linkAnimation
                    colorTheme = model.colorTheme
                    customPrimaryColor = model.customPrimaryColor
                    customSecondaryColor = model.customSecondaryColor
                    darkMode = model.darkMode
                    updatedAt = Instant.now()
                }

                existing.links.clear()
                model.links.forEach { linkModel ->
                    existing.links.add(linkModel.toEntity(existing))
                }

                existing.socials.clear()
                model.socials.forEach { socialModel ->
                    existing.socials.add(socialModel.toEntity(existing))
                }

                existing
            } else {
                model.toEntity()
            }
        } else {
            model.toEntity()
        }

        return profileRepository.save(entity).toDomain()
    }
}
