package dev.hddc.domains.hotdeal.application.service.command

import dev.hddc.domains.hotdeal.application.ports.input.command.DealCommentUsecase
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommandPort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommentPort
import dev.hddc.domains.hotdeal.domain.event.DealSseEvent
import dev.hddc.domains.hotdeal.domain.model.CreateHotDealCommentModel
import dev.hddc.domains.hotdeal.domain.model.HotDealCommentModel
import dev.hddc.domains.user.application.ports.output.query.UserQueryPort
import dev.hddc.domains.hotdeal.application.ports.output.event.DomainEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DealCommentService(
    private val hotDealCommandPort: HotDealCommandPort,
    private val hotDealCommentPort: HotDealCommentPort,
    private val userQueryPort: UserQueryPort,
    private val eventPublisher: DomainEventPublisher,
) : DealCommentUsecase {

    @Transactional
    override fun addComment(userId: Long, dealId: Long, content: String, parentId: Long?): HotDealCommentModel {
        val deal = hotDealCommandPort.loadById(dealId)

        if (parentId != null) {
            val parent = hotDealCommentPort.loadById(parentId)
            require(parent.belongsTo(dealId) && !parent.isDeleted) {
                "Parent comment not found or deleted"
            }
        }

        val saved = hotDealCommentPort.create(
            CreateHotDealCommentModel(
                dealId = dealId,
                userId = userId,
                parentId = parentId,
                content = content,
            )
        )
        val newCount = deal.commentCount + 1
        hotDealCommandPort.updateCommentCount(dealId, newCount)

        val nicknames = userQueryPort.findNicknamesByIds(listOf(userId))
        eventPublisher.publish(DealSseEvent.NewComment(
            dealId = dealId,
            id = saved.id,
            nickname = nicknames[userId] ?: "알 수 없음",
            content = content,
            parentId = parentId,
            createdAt = saved.createdAt,
        ))
        eventPublisher.publish(DealSseEvent.DealUpdated(id = dealId, commentCount = newCount))

        return saved
    }

    @Transactional
    override fun deleteComment(userId: Long, dealId: Long, commentId: Long) {
        val deal = hotDealCommandPort.loadById(dealId)
        val comment = hotDealCommentPort.loadById(commentId)
        require(comment.belongsTo(dealId) && comment.isOwnedBy(userId) && !comment.isDeleted) {
            "Comment not found or not authorized"
        }

        hotDealCommentPort.softDelete(commentId)
        val newCount = maxOf(0, deal.commentCount - 1)
        hotDealCommandPort.updateCommentCount(dealId, newCount)
        eventPublisher.publish(DealSseEvent.CommentDeleted(dealId = dealId, id = commentId))
        eventPublisher.publish(DealSseEvent.DealUpdated(id = dealId, commentCount = newCount))
    }
}
