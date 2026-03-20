package dev.hddc.domains.profile.application.ports.output.command

import dev.hddc.domains.profile.domain.model.ProfileLinkReportModel
import dev.hddc.domains.profile.domain.model.ProfileReportModel

interface ProfileReportPort {
    fun saveProfileReport(model: ProfileReportModel): ProfileReportModel
    fun saveLinkReport(model: ProfileLinkReportModel): ProfileLinkReportModel
}
