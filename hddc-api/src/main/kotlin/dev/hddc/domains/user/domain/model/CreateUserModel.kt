package dev.hddc.domains.user.domain.model

data class CreateUserModel(
    val email: String,
    val password: String,
    val nickname: String,
    val role: String = UserRole.USER.name,
)
