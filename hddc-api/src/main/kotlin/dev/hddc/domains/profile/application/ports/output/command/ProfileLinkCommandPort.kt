package dev.hddc.domains.profile.application.ports.output.command

import dev.hddc.domains.profile.domain.model.ProfileLinkModel

interface ProfileLinkCommandPort {
    fun findById(linkId: Long): ProfileLinkModel?
    fun save(model: ProfileLinkModel): ProfileLinkModel
    fun saveAll(models: List<ProfileLinkModel>): List<ProfileLinkModel>
    fun countByProfileId(profileId: Long): Long
    fun findAllByProfileId(profileId: Long): List<ProfileLinkModel>
}
