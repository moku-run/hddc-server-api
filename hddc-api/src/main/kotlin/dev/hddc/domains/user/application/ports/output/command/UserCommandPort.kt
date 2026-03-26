package dev.hddc.domains.user.application.ports.output.command

import dev.hddc.domains.user.domain.model.UserModel

interface UserCommandPort {
    fun create(model: UserModel): UserModel
    fun updateLoginSuccess(userId: Long)
    fun updateLoginFailed(userId: Long, attemptCount: Int, locked: Boolean)
    fun updatePassword(userId: Long, encodedPassword: String)
}
