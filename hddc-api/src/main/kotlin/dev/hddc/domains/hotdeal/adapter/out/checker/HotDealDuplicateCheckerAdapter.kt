package dev.hddc.domains.hotdeal.adapter.out.checker

import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.repository.HotDealRepository
import dev.hddc.domains.hotdeal.application.ports.output.checker.HotDealDuplicateChecker
import org.springframework.stereotype.Component

@Component
class HotDealDuplicateCheckerAdapter(
    private val hotDealRepository: HotDealRepository,
) : HotDealDuplicateChecker {

    override fun existsByUrl(url: String): Boolean =
        hotDealRepository.existsByUrlAndIsDeletedFalse(url)
}
