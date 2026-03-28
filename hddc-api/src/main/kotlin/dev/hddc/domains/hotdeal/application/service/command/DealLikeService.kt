package dev.hddc.domains.hotdeal.application.service.command

import dev.hddc.domains.hotdeal.application.ports.input.command.DealLikeUsecase
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommandPort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealLikePort
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealLikeQueryPort
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealQueryPort
import dev.hddc.domains.hotdeal.domain.event.DealSseEvent
import dev.hddc.domains.hotdeal.domain.model.HotDealLikeModel
import dev.hddc.domains.hotdeal.application.ports.output.event.DomainEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DealLikeService(
    private val hotDealQueryPort: HotDealQueryPort,
    private val hotDealCommandPort: HotDealCommandPort,
    private val hotDealLikePort: HotDealLikePort,
    private val hotDealLikeQueryPort: HotDealLikeQueryPort,
    private val eventPublisher: DomainEventPublisher,
) : DealLikeUsecase {

    @Transactional
    override fun like(userId: Long, dealId: Long) {
        val deal = hotDealQueryPort.loadById(dealId)
        if (hotDealLikeQueryPort.existsByDealIdAndUserId(dealId, userId)) return

        hotDealLikePort.save(HotDealLikeModel(dealId = dealId, userId = userId))
        val newCount = deal.likeCount + 1
        hotDealCommandPort.updateLikeCount(dealId, newCount)
        eventPublisher.publish(DealSseEvent.DealUpdated(id = dealId, likeCount = newCount))
    }

    @Transactional
    override fun unlike(userId: Long, dealId: Long) {
        val deal = hotDealQueryPort.loadById(dealId)
        val like = hotDealLikeQueryPort.findByDealIdAndUserId(dealId, userId) ?: return

        hotDealLikePort.delete(like)
        val newCount = maxOf(0, deal.likeCount - 1)
        hotDealCommandPort.updateLikeCount(dealId, newCount)
        eventPublisher.publish(DealSseEvent.DealUpdated(id = dealId, likeCount = newCount))
    }
}
