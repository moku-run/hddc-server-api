package dev.hddc.domains.profile.application.ports.output.command

import dev.hddc.domains.profile.domain.model.ProfileModel

interface ProfileCommandPort {
    fun save(model: ProfileModel): ProfileModel
}
