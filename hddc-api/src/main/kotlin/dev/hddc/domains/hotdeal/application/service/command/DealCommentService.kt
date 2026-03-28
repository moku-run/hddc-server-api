package dev.hddc.domains.hotdeal.application.service.command

import dev.hddc.domains.hotdeal.application.ports.input.command.DealCommentUsecase
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommandPort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommentPort
import dev.hddc.domains.hotdeal.application.ports.output.event.DomainEventPublisher
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealQueryPort
import dev.hddc.domains.hotdeal.application.ports.output.validator.HotDealCommentValidator
import dev.hddc.domains.hotdeal.domain.event.DealEvent
import dev.hddc.domains.hotdeal.domain.model.CreateHotDealCommentModel
import dev.hddc.domains.hotdeal.domain.model.HotDealCommentModel
import dev.hddc.domains.user.application.ports.output.query.UserQueryPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DealCommentService(
    private val hotDealQueryPort: HotDealQueryPort,
    private val hotDealCommandPort: HotDealCommandPort,
    private val hotDealCommentPort: HotDealCommentPort,
    private val hotDealCommentValidator: HotDealCommentValidator,
    private val userQueryPort: UserQueryPort,
    private val eventPublisher: DomainEventPublisher,
) : DealCommentUsecase {

    @Transactional
    override fun addComment(userId: Long, dealId: Long, content: String, parentId: Long?): HotDealCommentModel {
        val deal = hotDealQueryPort.loadById(dealId)
        if (parentId != null) hotDealCommentValidator.validateParentComment(parentId, dealId)

        val saved = hotDealCommentPort.create(CreateHotDealCommentModel(dealId, userId, parentId, content))
        val newCount = deal.incrementedCommentCount()
        hotDealCommandPort.updateCommentCount(dealId, newCount)

        val nicknames = userQueryPort.findNicknamesByIds(listOf(userId))
        eventPublisher.publish(DealEvent.NewComment(
            dealId = dealId,
            id = saved.id,
            nickname = nicknames[userId] ?: "알 수 없음",
            content = content,
            parentId = parentId,
            createdAt = saved.createdAt,
        ))
        eventPublisher.publish(DealEvent.DealUpdated(id = dealId, commentCount = newCount))

        return saved
    }

    @Transactional
    override fun deleteComment(userId: Long, dealId: Long, commentId: Long) {
        val deal = hotDealQueryPort.loadById(dealId)
        hotDealCommentValidator.validateCommentOwnership(commentId, userId, dealId)

        hotDealCommentPort.softDelete(commentId)
        val newCount = deal.decrementedCommentCount()
        hotDealCommandPort.updateCommentCount(dealId, newCount)
        eventPublisher.publish(DealEvent.CommentDeleted(dealId = dealId, id = commentId))
        eventPublisher.publish(DealEvent.DealUpdated(id = dealId, commentCount = newCount))
    }
}
