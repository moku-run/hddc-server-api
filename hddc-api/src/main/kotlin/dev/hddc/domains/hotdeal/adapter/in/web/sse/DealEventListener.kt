package dev.hddc.domains.hotdeal.adapter.`in`.web.sse

import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealCommentQueryPort
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealQueryPort
import dev.hddc.domains.hotdeal.domain.event.DealEvent
import dev.hddc.domains.user.application.ports.output.query.UserQueryPort
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class DealEventListener(
    private val sseEmitterManager: SseEmitterManager,
    private val hotDealQueryPort: HotDealQueryPort,
    private val hotDealCommentQueryPort: HotDealCommentQueryPort,
    private val userQueryPort: UserQueryPort,
) {
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handle(event: DealEvent) {
        when (event) {
            is DealEvent.DealCreated -> handleDealCreated(event)
            is DealEvent.DealDeleted -> broadcast(SseEventType.DEAL_DELETED, DealIdSseData(event.dealId))
            is DealEvent.DealExpired -> broadcast(SseEventType.DEAL_EXPIRED, DealIdSseData(event.dealId))
            is DealEvent.LikeCountChanged -> broadcast(SseEventType.DEAL_UPDATED, DealUpdatedSseData(id = event.dealId, likeCount = event.count))
            is DealEvent.CommentCountChanged -> broadcast(SseEventType.DEAL_UPDATED, DealUpdatedSseData(id = event.dealId, commentCount = event.count))
            is DealEvent.ClickCountChanged -> broadcast(SseEventType.DEAL_UPDATED, DealUpdatedSseData(id = event.dealId, clickCount = event.count))
            is DealEvent.ExpiredVoteCountChanged -> broadcast(SseEventType.DEAL_UPDATED, DealUpdatedSseData(id = event.dealId, expiredVoteCount = event.count))
            is DealEvent.CommentAdded -> handleCommentAdded(event)
            is DealEvent.CommentDeleted -> broadcast(SseEventType.COMMENT_DELETED, CommentDeletedSseData(event.dealId, event.commentId))
        }
    }

    private fun handleDealCreated(event: DealEvent.DealCreated) {
        val deal = hotDealQueryPort.findById(event.dealId) ?: return
        val nicknames = userQueryPort.findNicknamesByIds(listOf(deal.userId))
        broadcast(SseEventType.NEW_DEAL, NewDealSseData(
            id = deal.id,
            title = deal.title,
            dealPrice = deal.dealPrice,
            originalPrice = deal.originalPrice,
            discountRate = deal.discountRate,
            imageUrl = deal.imageUrl,
            nickname = nicknames[deal.userId] ?: "알 수 없음",
            store = deal.store,
            likeCount = deal.likeCount,
            commentCount = deal.commentCount,
            clickCount = deal.clickCount,
            createdAt = deal.createdAt,
        ))
    }

    private fun handleCommentAdded(event: DealEvent.CommentAdded) {
        val comment = try { hotDealCommentQueryPort.loadById(event.commentId) } catch (_: Exception) { return }
        val nicknames = userQueryPort.findNicknamesByIds(listOf(comment.userId))
        broadcast(SseEventType.NEW_COMMENT, NewCommentSseData(
            dealId = event.dealId,
            id = comment.id,
            nickname = nicknames[comment.userId] ?: "알 수 없음",
            content = comment.content,
            parentId = comment.parentId,
            createdAt = comment.createdAt,
        ))
    }

    private fun broadcast(eventType: String, data: Any) {
        sseEmitterManager.broadcast(eventType, data)
    }
}
