package dev.hddc.domains.profile.application.ports.input.command

import dev.hddc.domains.profile.domain.model.SocialLinkModel

interface SocialCommandUsecase {
    fun addSocial(userId: Long, command: AddSocialCommand): SocialLinkModel
    fun updateSocial(userId: Long, socialId: Long, command: UpdateSocialCommand): SocialLinkModel
    fun deleteSocial(userId: Long, socialId: Long)
    fun reorderSocials(userId: Long, orderedIds: List<Long>): List<SocialLinkModel>
}

data class AddSocialCommand(
    val platform: String,
    val url: String,
)

data class UpdateSocialCommand(
    val url: String,
)
