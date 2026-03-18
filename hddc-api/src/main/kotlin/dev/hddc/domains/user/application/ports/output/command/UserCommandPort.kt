package dev.hddc.domains.user.application.ports.output.command

import dev.hddc.domains.user.domain.model.UserModel

interface UserCommandPort {
    fun save(model: UserModel): UserModel
}
