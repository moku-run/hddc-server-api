package dev.hddc.domains.user.application.ports.output.validation

import dev.hddc.domains.user.domain.model.UserModel

interface LoginValidator {
    fun requireActiveUser(user: UserModel)
    fun requirePasswordMatch(rawPassword: String, encodedPassword: String)
}
