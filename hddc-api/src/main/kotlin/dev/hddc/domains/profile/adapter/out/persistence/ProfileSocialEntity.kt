package dev.hddc.domains.profile.adapter.out.persistence

import dev.hddc.framework.jpa.BaseAuditEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "profile_social")
class ProfileSocialEntity(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    var profile: ProfileEntity? = null,

    @Column(nullable = false, length = 30)
    var platform: String,

    @Column(nullable = false, length = 1000)
    var url: String,

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 0,
) : BaseAuditEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProfileSocialEntity) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()
}
