package dev.hddc.domains.profile.application.service.command

import dev.hddc.domains.profile.application.ports.input.command.AddLinkCommand
import dev.hddc.domains.profile.application.ports.input.command.LinkCommandUsecase
import dev.hddc.domains.profile.application.ports.input.command.ToggleLinkResult
import dev.hddc.domains.profile.application.ports.input.command.UpdateLinkCommand
import dev.hddc.domains.profile.application.ports.output.command.ProfileLinkCommandPort
import dev.hddc.domains.profile.application.ports.output.query.ProfileQueryPort
import dev.hddc.domains.profile.domain.model.ProfileLinkModel
import dev.hddc.framework.api.response.ApiResponseCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class LinkCommandService(
    private val profileQueryPort: ProfileQueryPort,
    private val profileLinkCommandPort: ProfileLinkCommandPort,
) : LinkCommandUsecase {

    @Transactional
    override fun addLink(userId: Long, command: AddLinkCommand): ProfileLinkModel {
        val profile = profileQueryPort.findByUserId(userId)
            ?: throw IllegalArgumentException(ApiResponseCode.PROFILE_NOT_FOUND.code)

        val count = profileLinkCommandPort.countByProfileId(profile.id!!)
        require(count < 20) { ApiResponseCode.PROFILE_LINK_LIMIT_EXCEEDED.code }

        val model = ProfileLinkModel(
            profileId = profile.id,
            title = command.title,
            url = command.url,
            description = command.description,
            sortOrder = count.toInt(),
        )
        return profileLinkCommandPort.save(model)
    }

    @Transactional
    override fun updateLink(userId: Long, linkId: Long, command: UpdateLinkCommand): ProfileLinkModel {
        val link = findOwnedLink(userId, linkId)
        val updated = link.copy(
            title = command.title ?: link.title,
            url = command.url ?: link.url,
            description = command.description ?: link.description,
            updatedAt = Instant.now(),
        )
        return profileLinkCommandPort.save(updated)
    }

    @Transactional
    override fun deleteLink(userId: Long, linkId: Long) {
        val link = findOwnedLink(userId, linkId)
        profileLinkCommandPort.save(link.copy(isDeleted = true))
    }

    @Transactional
    override fun toggleLink(userId: Long, linkId: Long): ToggleLinkResult {
        val link = findOwnedLink(userId, linkId)
        val toggled = profileLinkCommandPort.save(link.copy(enabled = !link.enabled, updatedAt = Instant.now()))
        return ToggleLinkResult(id = toggled.id!!, enabled = toggled.enabled)
    }

    @Transactional
    override fun reorderLinks(userId: Long, orderedIds: List<Long>): List<ProfileLinkModel> {
        val profile = profileQueryPort.findByUserId(userId)
            ?: throw IllegalArgumentException(ApiResponseCode.PROFILE_NOT_FOUND.code)
        val links = profileLinkCommandPort.findAllByProfileId(profile.id!!)
        val reordered = links.map { link ->
            val newOrder = orderedIds.indexOf(link.id)
            if (newOrder >= 0) link.copy(sortOrder = newOrder, updatedAt = Instant.now()) else link
        }
        return profileLinkCommandPort.saveAll(reordered).sortedBy { it.sortOrder }
    }

    private fun findOwnedLink(userId: Long, linkId: Long): ProfileLinkModel {
        val profile = profileQueryPort.findByUserId(userId)
            ?: throw IllegalArgumentException(ApiResponseCode.PROFILE_NOT_FOUND.code)
        val link = profileLinkCommandPort.findById(linkId)
            ?: throw IllegalArgumentException(ApiResponseCode.PROFILE_LINK_NOT_FOUND.code)
        require(link.profileId == profile.id && !link.isDeleted) {
            ApiResponseCode.PROFILE_LINK_NOT_FOUND.code
        }
        return link
    }
}
