package dev.hddc.domains.hotdeal.application.service.command

import dev.hddc.domains.hotdeal.application.ports.input.command.DealClickResult
import dev.hddc.domains.hotdeal.application.ports.input.command.DealClickUsecase
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealClickPort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommandPort
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealQueryPort
import dev.hddc.domains.hotdeal.application.ports.output.event.DomainEventPublisher
import dev.hddc.domains.hotdeal.domain.event.DealSseEvent
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
        if (!deal.isActive) return null

        // TODO: 테스트 완료 후 중복 체크 복원
        // if (hotDealClickChecker.isDuplicate(dealId, userId, ip)) {
        //     return DealClickResult(url = deal.url, dealId = dealId, clickCount = deal.clickCount)
        // }

        hotDealClickPort.save(dealId, userId, ip)
        val newCount = deal.clickCount + 1
        hotDealCommandPort.updateClickCount(dealId, newCount)
        eventPublisher.publish(DealSseEvent.DealUpdated(id = dealId, clickCount = newCount))

        return DealClickResult(url = deal.url, dealId = dealId, clickCount = newCount)
    }
}
