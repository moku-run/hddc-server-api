package dev.hddc.domains.profile.application.ports.output.command

import dev.hddc.domains.profile.domain.model.SocialLinkModel

interface ProfileSocialCommandPort {
    fun findById(socialId: Long): SocialLinkModel?
    fun save(model: SocialLinkModel): SocialLinkModel
    fun saveAll(models: List<SocialLinkModel>): List<SocialLinkModel>
    fun countByProfileId(profileId: Long): Long
    fun findAllByProfileId(profileId: Long): List<SocialLinkModel>
    fun existsByProfileIdAndPlatform(profileId: Long, platform: String): Boolean
}
