package dev.hddc.framework.jpa

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
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

    @Column(name = "remark1", length = 500)
    var remark1: String? = null

    @Column(name = "remark2", length = 500)
    var remark2: String? = null

    @Column(name = "remark3", length = 500)
    var remark3: String? = null
}
