package dev.hddc.domains.user.domain.model

data class UserModel(
    val id: Long,
    val email: String,
    val password: String,
    val nickname: String,
    val role: String,
    val isDeleted: Boolean,
    val isLocked: Boolean,
    val loginAttemptCount: Int,
)
