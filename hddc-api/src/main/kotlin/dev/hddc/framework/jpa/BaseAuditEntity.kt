package dev.hddc.framework.jpa

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import java.time.Instant

@MappedSuperclass
abstract class BaseAuditEntity : BaseEntity() {
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()

    @Column(name = "created_by", nullable = false)
    var createdBy: Long = 0

    @Column(name = "updated_by", nullable = false)
    var updatedBy: Long = 0

    @Column(name = "is_deleted", nullable = false)
    var isDeleted: Boolean = false

    @Column(name = "deleted_at")
    var deletedAt: Instant? = null

    @PrePersist
    fun onAuditPrePersist() {
        val now = Instant.now()
        updatedAt = now
        val userId = AuditContextHolder.getCurrentUserId()
        createdBy = userId
        updatedBy = userId
    }

    @PreUpdate
    fun onAuditPreUpdate() {
        updatedAt = Instant.now()
        updatedBy = AuditContextHolder.getCurrentUserId()
    }
}
