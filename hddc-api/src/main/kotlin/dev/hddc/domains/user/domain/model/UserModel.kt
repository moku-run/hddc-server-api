package dev.hddc.domains.user.domain.model

import java.time.Instant

data class UserModel(
    val id: Long? = null,
    val email: String,
    val password: String,
    val nickname: String,
    val role: String = UserRole.USER.name,
    val isDeleted: Boolean = false,
    val isLocked: Boolean = false,
    val loginAttemptCount: Int = 0,
    val lastLoginAt: Instant? = null,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
)
