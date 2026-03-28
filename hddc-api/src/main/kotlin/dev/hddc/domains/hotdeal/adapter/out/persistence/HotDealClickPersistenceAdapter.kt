package dev.hddc.domains.hotdeal.adapter.out.persistence

import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealClickPort
import dev.hddc.domains.hotdeal.domain.spec.HotDealClickSpec
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class HotDealClickPersistenceAdapter(
    private val hotDealClickRepository: HotDealClickRepository,
) : HotDealClickPort {

    override fun isDuplicate(dealId: Long, userId: Long?, ip: String): Boolean {
        val after = Instant.now().minusSeconds(HotDealClickSpec.DEDUP_MINUTES * 60)

        return if (userId != null) {
            hotDealClickRepository.existsByDealIdAndUserIdAndCreatedAtAfter(dealId, userId, after)
        } else {
            hotDealClickRepository.existsByDealIdAndIpAndUserIdIsNullAndCreatedAtAfter(dealId, ip, after)
        }
    }

    override fun save(dealId: Long, userId: Long?, ip: String) {
        hotDealClickRepository.save(HotDealClickEntity(dealId = dealId, userId = userId, ip = ip))
    }
}
