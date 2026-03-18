package dev.hddc.domains.profile.application.ports.input.command

import dev.hddc.domains.profile.domain.model.ProfileLinkModel

interface LinkCommandUsecase {
    fun addLink(userId: Long, command: AddLinkCommand): ProfileLinkModel
    fun updateLink(userId: Long, linkId: Long, command: UpdateLinkCommand): ProfileLinkModel
    fun deleteLink(userId: Long, linkId: Long)
    fun toggleLink(userId: Long, linkId: Long): ToggleLinkResult
    fun reorderLinks(userId: Long, orderedIds: List<Long>): List<ProfileLinkModel>
}

data class AddLinkCommand(
    val title: String,
    val url: String,
    val description: String? = null,
)

data class UpdateLinkCommand(
    val title: String? = null,
    val url: String? = null,
    val description: String? = null,
)

data class ToggleLinkResult(
    val id: Long,
    val enabled: Boolean,
)
