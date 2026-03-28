package dev.hddc.domains.hotdeal.application.service.query

import dev.hddc.domains.hotdeal.application.ports.input.query.CandidateDealAdminQueryUsecase
import dev.hddc.domains.hotdeal.application.ports.output.query.CandidateDealPageData
import dev.hddc.domains.hotdeal.application.ports.output.query.CandidateDealQueryPort
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CandidateDealAdminQueryService(
    private val candidateDealQueryPort: CandidateDealQueryPort,
) : CandidateDealAdminQueryUsecase {

    @Transactional(readOnly = true)
    override fun getCandidateDeals(status: String, pageable: Pageable): CandidateDealPageData =
        candidateDealQueryPort.findByStatus(status, pageable)
}
