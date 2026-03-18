package dev.hddc.domains.hotdeal.domain.model

import java.time.Instant

data class HotDealModel(
    val id: Long? = null,
    val userId: Long,
    val title: String,
    val description: String? = null,
    val url: String,
    val imageUrl: String? = null,
    val originalPrice: Int? = null,
    val dealPrice: Int? = null,
    val discountRate: Int? = null,
    val category: String? = null,
    val store: String? = null,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val expiredVoteCount: Int = 0,
    val isExpired: Boolean = false,
    val isDeleted: Boolean = false,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
)
