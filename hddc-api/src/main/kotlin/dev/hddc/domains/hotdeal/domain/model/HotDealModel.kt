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
) {
    val isActive: Boolean get() = !isDeleted && !isExpired

    val isNotExpired: Boolean get() = !isExpired

    val isInactive: Boolean get() = isDeleted || isExpired

    val hasDiscount: Boolean get() = discountRate != null && discountRate > 0

    fun incrementedLikeCount(): Int = likeCount + 1
    fun decrementedLikeCount(): Int = maxOf(0, likeCount - 1)
    fun incrementedCommentCount(): Int = commentCount + 1
    fun decrementedCommentCount(): Int = maxOf(0, commentCount - 1)
    fun incrementedClickCount(): Int = clickCount + 1
    fun incrementedExpiredVoteCount(): Int = expiredVoteCount + 1
    fun decrementedExpiredVoteCount(): Int = maxOf(0, expiredVoteCount - 1)
}
