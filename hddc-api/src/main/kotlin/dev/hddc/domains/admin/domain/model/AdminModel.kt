package dev.hddc.domains.admin.domain.model

import java.time.Instant

data class AdminModel(
    val id: Long? = null,
    val email: String,
    val password: String,
    val name: String,
    val role: String = AdminRole.ADMIN.name,
    val isActive: Boolean = true,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
)

enum class AdminRole {
    SUPER_ADMIN,
    ADMIN,
    MODERATOR,
}
