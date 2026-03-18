package dev.hddc.domains.profile.application.ports.input.command

import dev.hddc.domains.profile.domain.model.ProfileModel

interface ResetProfileUsecase {
    fun execute(userId: Long): ProfileModel
}
