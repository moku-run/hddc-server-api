package dev.hddc.domains.user.domain.model

data class UserModel(
    val id: Long? = null,
    val email: String,
    val password: String,
    val nickname: String,
    val role: String = UserRole.USER.name,
    val isDeleted: Boolean = false,
    val isLocked: Boolean = false,
    val loginAttemptCount: Int = 0,
)
