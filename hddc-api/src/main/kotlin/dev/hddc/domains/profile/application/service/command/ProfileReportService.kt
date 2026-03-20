package dev.hddc.domains.profile.application.service.command

import dev.hddc.domains.profile.application.ports.input.command.ProfileReportUsecase
import dev.hddc.domains.profile.application.ports.output.command.ProfileLinkCommandPort
import dev.hddc.domains.profile.application.ports.output.command.ProfileReportPort
import dev.hddc.domains.profile.application.ports.output.query.ProfileQueryPort
import dev.hddc.domains.profile.domain.model.ProfileLinkReportModel
import dev.hddc.domains.profile.domain.model.ProfileReportModel
import dev.hddc.framework.api.response.ApiResponseCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProfileReportService(
    private val profileQueryPort: ProfileQueryPort,
    private val profileLinkCommandPort: ProfileLinkCommandPort,
    private val profileReportPort: ProfileReportPort,
) : ProfileReportUsecase {

    @Transactional
    override fun reportProfile(userId: Long, slug: String, reason: String) {
        val profile = profileQueryPort.findBySlugWithDetails(slug)
            ?: throw IllegalArgumentException(ApiResponseCode.PROFILE_NOT_FOUND.code)

        require(profile.userId != userId) {
            ApiResponseCode.INVALID_REQUEST.code
        }

        profileReportPort.saveProfileReport(
            ProfileReportModel(profileId = profile.id!!, userId = userId, reason = reason)
        )
    }

    @Transactional
    override fun reportLink(userId: Long, slug: String, linkId: Long, reason: String) {
        val profile = profileQueryPort.findBySlugWithDetails(slug)
            ?: throw IllegalArgumentException(ApiResponseCode.PROFILE_NOT_FOUND.code)

        require(profile.userId != userId) {
            ApiResponseCode.INVALID_REQUEST.code
        }

        val link = profileLinkCommandPort.findById(linkId)
            ?: throw IllegalArgumentException(ApiResponseCode.PROFILE_LINK_NOT_FOUND.code)

        require(link.profileId == profile.id && !link.isDeleted) {
            ApiResponseCode.PROFILE_LINK_NOT_FOUND.code
        }

        profileReportPort.saveLinkReport(
            ProfileLinkReportModel(linkId = linkId, userId = userId, reason = reason)
        )
    }
}
