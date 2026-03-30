package dev.hddc.domains.hotdeal.application.service.command

import dev.hddc.domains.hotdeal.application.ports.input.command.DealClickSyncUsecase
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealClickPort
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealClickQueryPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DealClickSyncService(
    private val hotDealClickQueryPort: HotDealClickQueryPort,
    private val hotDealClickPort: HotDealClickPort,
) : DealClickSyncUsecase {

    @Transactional
    override fun sync(userId: Long, dealIds: List<Long>): Int {
        if (dealIds.isEmpty()) return 0

        val alreadyClicked = hotDealClickQueryPort.findDealIdsByUserIdAndDealIds(userId, dealIds)
        val newDealIds = dealIds.filter { it !in alreadyClicked }

        newDealIds.forEach { dealId ->
            hotDealClickPort.save(dealId, userId, ip = "sync")
        }

        return newDealIds.size
    }
}
