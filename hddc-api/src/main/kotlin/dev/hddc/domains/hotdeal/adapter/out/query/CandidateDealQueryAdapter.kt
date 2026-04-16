package dev.hddc.domains.hotdeal.adapter.out.query

import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.mapper.toDomain
import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.repository.CandidateDealRepository
import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.repository.loadById
import dev.hddc.domains.hotdeal.application.ports.output.query.CandidateDealPageData
import dev.hddc.domains.hotdeal.application.ports.output.query.CandidateDealQueryPort
import dev.hddc.domains.hotdeal.domain.model.CandidateDealModel
import dev.hddc.domains.hotdeal.domain.model.CandidateDealStatus
import dev.hddc.framework.pagination.Pagination
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class CandidateDealQueryAdapter(
    private val candidateDealRepository: CandidateDealRepository,
) : CandidateDealQueryPort {

    override fun findById(id: Long): CandidateDealModel? =
        candidateDealRepository.findById(id).orElse(null)?.toDomain()

    override fun loadById(id: Long): CandidateDealModel =
        candidateDealRepository.loadById(id).toDomain()

    override fun findByStatus(status: CandidateDealStatus, pageable: Pageable): CandidateDealPageData {
        val page = candidateDealRepository.findByStatus(status.name, pageable)
        return CandidateDealPageData(
            content = page.content.map { it.toDomain() },
            pagination = Pagination.of(page),
        )
    }
}
