package dev.hddc.domains.hotdeal.adapter.`in`.web.sse

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SsePayload(
    val eventType: String,
    val data: Any,
)

object SseEventType {
    const val NEW_DEAL = "new-deal"
    const val DEAL_UPDATED = "deal-updated"
    const val DEAL_EXPIRED = "deal-expired"
    const val DEAL_DELETED = "deal-deleted"
    const val NEW_COMMENT = "new-comment"
    const val COMMENT_DELETED = "comment-deleted"
}

data class NewDealSseData(
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
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class DealUpdatedSseData(
    val id: Long,
    val likeCount: Int? = null,
    val clickCount: Int? = null,
    val expiredVoteCount: Int? = null,
    val commentCount: Int? = null,
)

data class DealIdSseData(val id: Long)

data class NewCommentSseData(
    val dealId: Long,
    val id: Long,
    val nickname: String,
    val content: String,
    val parentId: Long?,
    val createdAt: Instant,
)

data class CommentDeletedSseData(
    val dealId: Long,
    val id: Long,
)
