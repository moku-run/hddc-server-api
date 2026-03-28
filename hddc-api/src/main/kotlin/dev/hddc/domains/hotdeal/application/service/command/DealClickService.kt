package dev.hddc.domains.hotdeal.application.service.command

import dev.hddc.domains.hotdeal.application.ports.input.command.DealClickResult
import dev.hddc.domains.hotdeal.application.ports.input.command.DealClickUsecase
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealClickPort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommandPort
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealQueryPort
import dev.hddc.domains.hotdeal.application.ports.output.event.DomainEventPublisher
import dev.hddc.domains.hotdeal.domain.event.DealEvent
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DealClickService(
    private val hotDealQueryPort: HotDealQueryPort,
    private val hotDealCommandPort: HotDealCommandPort,
    private val hotDealClickPort: HotDealClickPort,
    private val eventPublisher: DomainEventPublisher,
) : DealClickUsecase {

    @Transactional
    override fun click(dealId: Long, userId: Long?, ip: String): DealClickResult? {
        val deal = hotDealQueryPort.findById(dealId) ?: return null
        if (deal.isInactive) return null

        hotDealClickPort.save(dealId, userId, ip)
        val newCount = deal.incrementedClickCount()
        hotDealCommandPort.updateClickCount(dealId, newCount)
        eventPublisher.publish(DealEvent.DealUpdated(id = dealId, clickCount = newCount))

        return DealClickResult(url = deal.url, dealId = dealId, clickCount = newCount)
    }
}
