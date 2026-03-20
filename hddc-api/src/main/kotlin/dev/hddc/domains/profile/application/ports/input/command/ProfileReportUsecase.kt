package dev.hddc.domains.profile.application.ports.input.command

interface ProfileReportUsecase {
    fun reportProfile(userId: Long, slug: String, reason: String)
    fun reportLink(userId: Long, slug: String, linkId: Long, reason: String)
}
