package dev.hddc.domains.profile.domain.model

import java.time.Instant

data class ProfileLinkModel(
    val id: Long? = null,
    val profileId: Long? = null,
    val title: String,
    val url: String,
    val imageUrl: String? = null,
    val description: String? = null,
    val sortOrder: Int = 0,
    val enabled: Boolean = true,
    val price: Long? = null,
    val originalPrice: Long? = null,
    val discountRate: Int? = null,
    val store: String? = null,
    val category: String? = null,
    val clicks: Long = 0,
    val likes: Long = 0,
    val isDeleted: Boolean = false,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
)
