package dev.hddc.domains.hotdeal.application.service.command

import dev.hddc.domains.hotdeal.application.ports.input.command.DealExpiredVoteUsecase
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommandPort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealExpiredVotePort
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealExpiredVoteQueryPort
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealQueryPort
import dev.hddc.domains.hotdeal.domain.event.DealEvent
import dev.hddc.domains.hotdeal.domain.model.HotDealExpiredVoteModel
import dev.hddc.domains.hotdeal.domain.spec.HotDealSpec
import dev.hddc.domains.hotdeal.application.ports.output.event.DomainEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DealExpiredVoteService(
    private val hotDealQueryPort: HotDealQueryPort,
    private val hotDealCommandPort: HotDealCommandPort,
    private val hotDealExpiredVotePort: HotDealExpiredVotePort,
    private val hotDealExpiredVoteQueryPort: HotDealExpiredVoteQueryPort,
    private val eventPublisher: DomainEventPublisher,
) : DealExpiredVoteUsecase {

    @Transactional
    override fun vote(userId: Long, dealId: Long) {
        val deal = hotDealQueryPort.loadById(dealId)
        if (hotDealExpiredVoteQueryPort.existsByDealIdAndUserId(dealId, userId)) return

        hotDealExpiredVotePort.save(HotDealExpiredVoteModel(dealId = dealId, userId = userId))
        val newCount = deal.incrementedExpiredVoteCount()
        val expired = HotDealSpec.isExpiredThresholdReached(newCount)
        hotDealCommandPort.updateExpiredVote(dealId, newCount, expired)
        eventPublisher.publish(DealEvent.ExpiredVoteCountChanged(dealId = dealId, count = newCount))
        if (expired && !deal.isExpired) {
            eventPublisher.publish(DealEvent.DealExpired(dealId = dealId))
        }
    }

    @Transactional
    override fun unvote(userId: Long, dealId: Long) {
        val deal = hotDealQueryPort.loadById(dealId)
        if (!hotDealExpiredVotePort.deleteByDealIdAndUserId(dealId, userId)) return

        val newCount = deal.decrementedExpiredVoteCount()
        hotDealCommandPort.updateExpiredVote(dealId, newCount, false)
        eventPublisher.publish(DealEvent.ExpiredVoteCountChanged(dealId = dealId, count = newCount))
    }
}
