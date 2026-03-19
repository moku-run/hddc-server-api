package dev.hddc.domains.profile.adapter.out.persistence

import dev.hddc.framework.jpa.BaseAuditEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "mst_profile_link")
class ProfileLinkEntity(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    var profile: ProfileEntity? = null,

    @Column(nullable = false, length = 100)
    var title: String,

    @Column(nullable = false, length = 1000)
    var url: String,

    @Column(name = "image_url", length = 1000)
    var imageUrl: String? = null,

    @Column(length = 200)
    var description: String? = null,

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 0,

    @Column(nullable = false)
    var enabled: Boolean = true,
) : BaseAuditEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProfileLinkEntity) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()
}
