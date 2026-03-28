package dev.hddc.domains.hotdeal.domain.model

import java.time.Instant

data class HotDealModel(
    val id: Long,
    val userId: Long,
    val title: String,
    val description: String?,
    val url: String,
    val imageUrl: String?,
    val originalPrice: Int?,
    val dealPrice: Int?,
    val discountRate: Int?,
    val category: String?,
    val store: String?,
    val likeCount: Int,
    val commentCount: Int,
    val expiredVoteCount: Int,
    val clickCount: Int,
    val isExpired: Boolean,
    val isDeleted: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant,
)
