package dev.hddc.domains.hotdeal.domain.event

import java.time.Instant

sealed class DealSseEvent(val eventType: String) {

    data class NewDeal(
        val id: Long,
        val title: String,
        val dealPrice: Int?,
        val originalPrice: Int?,
        val discountRate: Int?,
        val imageUrl: String?,
        val nickname: String,
        val store: String?,
        val likeCount: Int,
        val commentCount: Int,
        val clickCount: Int,
        val createdAt: Instant,
    ) : DealSseEvent("new-deal")

    data class DealUpdated(
        val id: Long,
        val likeCount: Int? = null,
        val clickCount: Int? = null,
        val expiredVoteCount: Int? = null,
        val commentCount: Int? = null,
    ) : DealSseEvent("deal-updated")

    data class DealExpired(
        val id: Long,
    ) : DealSseEvent("deal-expired")

    data class DealDeleted(
        val id: Long,
    ) : DealSseEvent("deal-deleted")

    data class NewComment(
        val dealId: Long,
        val id: Long,
        val nickname: String,
        val content: String,
        val parentId: Long?,
        val createdAt: Instant,
    ) : DealSseEvent("new-comment")

    data class CommentDeleted(
        val dealId: Long,
        val id: Long,
    ) : DealSseEvent("comment-deleted")
}
