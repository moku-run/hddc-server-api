package dev.hddc.domains.hotdeal.application.service.command

import dev.hddc.domains.hotdeal.application.ports.input.command.DealExpiredVoteUsecase
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommandPort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealExpiredVotePort
import dev.hddc.domains.hotdeal.domain.event.DealSseEvent
import dev.hddc.domains.hotdeal.domain.model.HotDealExpiredVoteModel
import dev.hddc.framework.api.response.ApiResponseCode
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DealExpiredVoteService(
    private val hotDealCommandPort: HotDealCommandPort,
    private val hotDealExpiredVotePort: HotDealExpiredVotePort,
    private val eventPublisher: ApplicationEventPublisher,
) : DealExpiredVoteUsecase {

    companion object {
        private const val EXPIRED_THRESHOLD = 10
    }

    @Transactional
    override fun vote(userId: Long, dealId: Long) {
        val deal = hotDealCommandPort.findById(dealId)
            ?: throw IllegalArgumentException(ApiResponseCode.HOT_DEAL_NOT_FOUND.code)
        if (hotDealExpiredVotePort.existsByDealIdAndUserId(dealId, userId)) return

        hotDealExpiredVotePort.save(HotDealExpiredVoteModel(dealId = dealId, userId = userId))
        val newCount = deal.expiredVoteCount + 1
        val expired = newCount >= EXPIRED_THRESHOLD
        hotDealCommandPort.save(deal.copy(expiredVoteCount = newCount, isExpired = expired))
        eventPublisher.publishEvent(DealSseEvent.DealUpdated(id = dealId, expiredVoteCount = newCount))
        if (expired && !deal.isExpired) {
            eventPublisher.publishEvent(DealSseEvent.DealExpired(id = dealId))
        }
    }

    @Transactional
    override fun unvote(userId: Long, dealId: Long) {
        val deal = hotDealCommandPort.findById(dealId)
            ?: throw IllegalArgumentException(ApiResponseCode.HOT_DEAL_NOT_FOUND.code)
        val vote = hotDealExpiredVotePort.findByDealIdAndUserId(dealId, userId) ?: return

        hotDealExpiredVotePort.delete(vote)
        val newCount = maxOf(0, deal.expiredVoteCount - 1)
        hotDealCommandPort.save(deal.copy(expiredVoteCount = newCount, isExpired = false))
        eventPublisher.publishEvent(DealSseEvent.DealUpdated(id = dealId, expiredVoteCount = newCount))
    }
}
