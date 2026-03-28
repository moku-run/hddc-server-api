package dev.hddc.domains.hotdeal.domain.event

sealed class DealEvent {
    data class DealCreated(val dealId: Long) : DealEvent()
    data class DealDeleted(val dealId: Long) : DealEvent()
    data class LikeCountChanged(val dealId: Long, val count: Int) : DealEvent()
    data class CommentCountChanged(val dealId: Long, val count: Int) : DealEvent()
    data class ClickCountChanged(val dealId: Long, val count: Int) : DealEvent()
    data class ExpiredVoteCountChanged(val dealId: Long, val count: Int) : DealEvent()
    data class DealExpired(val dealId: Long) : DealEvent()
    data class CommentAdded(val dealId: Long, val commentId: Long) : DealEvent()
    data class CommentDeleted(val dealId: Long, val commentId: Long) : DealEvent()
}
