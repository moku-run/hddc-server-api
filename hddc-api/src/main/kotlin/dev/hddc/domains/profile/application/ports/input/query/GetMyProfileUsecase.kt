package dev.hddc.domains.profile.application.ports.input.query

import dev.hddc.domains.profile.domain.model.ProfileModel

interface GetMyProfileUsecase {
    fun execute(userId: Long): ProfileModel
}
