package dev.hddc.domains.user.application.ports.output.command

import dev.hddc.domains.user.domain.model.CreateUserModel

interface UserCommandPort {
    fun create(model: CreateUserModel): Long
    fun updateLoginSuccess(userId: Long)
    fun updateLoginFailed(userId: Long, attemptCount: Int, locked: Boolean)
    fun updatePassword(userId: Long, encodedPassword: String)
}
