package dev.hddc.domains.profile.application.ports.output.command

interface CreateDefaultProfilePort {
    fun createDefaultProfile(userId: Long, nickname: String)
}
