package dev.hddc.domains.profile.domain.model

import java.time.Instant

data class SocialLinkModel(
    val id: Long? = null,
    val profileId: Long? = null,
    val platform: String,
    val url: String,
    val sortOrder: Int = 0,
    val isDeleted: Boolean = false,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
)
