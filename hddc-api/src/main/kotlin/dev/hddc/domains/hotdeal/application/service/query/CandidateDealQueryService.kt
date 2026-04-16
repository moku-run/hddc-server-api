package dev.hddc.domains.hotdeal.application.service.query

import dev.hddc.domains.hotdeal.application.ports.input.query.CandidateDealAdminQueryUsecase
import dev.hddc.domains.hotdeal.application.ports.input.query.CandidateDealPageResult
import dev.hddc.domains.hotdeal.application.ports.output.query.CandidateDealQueryPort
import dev.hddc.domains.hotdeal.domain.model.CandidateDealStatus
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CandidateDealQueryService(
    private val candidateDealQueryPort: CandidateDealQueryPort,
) : CandidateDealAdminQueryUsecase {

    @Transactional(readOnly = true)
    override fun getAll(status: CandidateDealStatus, pageable: Pageable): CandidateDealPageResult {
        val page = candidateDealQueryPort.findByStatus(status, pageable)
        return CandidateDealPageResult(
            content = page.content,
            pagination = page.pagination,
        )
    }
}
