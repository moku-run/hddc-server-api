package dev.hddc.domains.hotdeal.domain.event

import java.time.Instant

sealed class DealEvent(val eventType: DealEventType) {

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
    ) : DealEvent(DealEventType.NEW_DEAL)

    data class DealUpdated(
        val id: Long,
        val likeCount: Int? = null,
        val clickCount: Int? = null,
        val expiredVoteCount: Int? = null,
        val commentCount: Int? = null,
    ) : DealEvent(DealEventType.DEAL_UPDATED)

    data class DealExpired(
        val id: Long,
    ) : DealEvent(DealEventType.DEAL_EXPIRED)

    data class DealDeleted(
        val id: Long,
    ) : DealEvent(DealEventType.DEAL_DELETED)

    data class NewComment(
        val dealId: Long,
        val id: Long,
        val nickname: String,
        val content: String,
        val parentId: Long?,
        val createdAt: Instant,
    ) : DealEvent(DealEventType.NEW_COMMENT)

    data class CommentDeleted(
        val dealId: Long,
        val id: Long,
    ) : DealEvent(DealEventType.COMMENT_DELETED)
}
