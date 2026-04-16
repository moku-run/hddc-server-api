package dev.hddc.domains.profile.adapter.out.persistence

import dev.hddc.framework.jpa.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "profile_report")
class ProfileReportEntity(
    @Column(name = "profile_id", nullable = false)
    val profileId: Long,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(nullable = false, length = 100)
    val reason: String,
) : BaseEntity()

@Entity
@Table(name = "profile_link_report")
class ProfileLinkReportEntity(
    @Column(name = "link_id", nullable = false)
    val linkId: Long,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(nullable = false, length = 100)
    val reason: String,
) : BaseEntity()
