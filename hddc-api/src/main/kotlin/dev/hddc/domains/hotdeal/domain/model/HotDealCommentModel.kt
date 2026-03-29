package dev.hddc.domains.hotdeal.domain.model

import java.time.Instant

data class HotDealCommentModel(
    val id: Long,
    val dealId: Long,
    val userId: Long,
    val parentId: Long?,
    val content: String,
    val likeCount: Int,
    val isDeleted: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    val isRootComment: Boolean get() = parentId == null

    val isReply: Boolean get() = parentId != null

    val isNotDeleted: Boolean get() = !isDeleted

    fun isOwnedBy(userId: Long): Boolean = this.userId == userId
    fun isNotOwnedBy(userId: Long): Boolean = !isOwnedBy(userId)

    fun belongsTo(dealId: Long): Boolean = this.dealId == dealId
    fun doesNotBelongTo(dealId: Long): Boolean = !belongsTo(dealId)

    fun incrementedLikeCount(): Int = likeCount + 1
    fun decrementedLikeCount(): Int = maxOf(0, likeCount - 1)
}
