package dev.hddc.domains.profile.adapter.out.persistence

import dev.hddc.domains.profile.application.ports.output.command.ProfileReportPort
import dev.hddc.domains.profile.domain.model.ProfileLinkReportModel
import dev.hddc.domains.profile.domain.model.ProfileReportModel
import org.springframework.stereotype.Repository

@Repository
class ProfileReportPersistenceAdapter(
    private val profileReportRepository: ProfileReportRepository,
    private val profileLinkReportRepository: ProfileLinkReportRepository,
) : ProfileReportPort {

    override fun saveProfileReport(model: ProfileReportModel): ProfileReportModel {
        val entity = ProfileReportEntity(
            profileId = model.profileId,
            userId = model.userId,
            reason = model.reason,
        )
        val saved = profileReportRepository.save(entity)
        return ProfileReportModel(
            id = saved.id,
            profileId = saved.profileId,
            userId = saved.userId,
            reason = saved.reason,
            createdAt = saved.createdAt,
        )
    }

    override fun saveLinkReport(model: ProfileLinkReportModel): ProfileLinkReportModel {
        val entity = ProfileLinkReportEntity(
            linkId = model.linkId,
            userId = model.userId,
            reason = model.reason,
        )
        val saved = profileLinkReportRepository.save(entity)
        return ProfileLinkReportModel(
            id = saved.id,
            linkId = saved.linkId,
            userId = saved.userId,
            reason = saved.reason,
            createdAt = saved.createdAt,
        )
    }
}
