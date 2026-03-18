package dev.hddc.domains.analytics.adapter.out.persistence

import dev.hddc.framework.jpa.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "his_link_click")
class LinkClickEntity(
    @Column(name = "profile_id", nullable = false)
    val profileId: Long,

    @Column(name = "link_id", nullable = false)
    val linkId: Long,

    @Column(nullable = false, length = 30)
    val slug: String,

    @Column(length = 45)
    val ip: String? = null,

    @Column(name = "user_agent", length = 500)
    val userAgent: String? = null,

    @Column(length = 1000)
    val referer: String? = null,
) : BaseEntity()
