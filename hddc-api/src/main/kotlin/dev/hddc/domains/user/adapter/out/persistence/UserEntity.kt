package dev.hddc.domains.user.adapter.out.persistence

import dev.hddc.domains.user.domain.model.UserRole
import dev.hddc.framework.jpa.BaseAuditEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "mst_user")
class UserEntity(
    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    var password: String,

    @Column(nullable = false, length = 50)
    var nickname: String,

    @Column(nullable = false, length = 20)
    var role: String = UserRole.USER.name,

    @Column(name = "is_locked", nullable = false)
    var isLocked: Boolean = false,

    @Column(name = "login_attempt_count", nullable = false)
    var loginAttemptCount: Int = 0,

    @Column(name = "last_login_at")
    var lastLoginAt: Instant? = null,
) : BaseAuditEntity()
