package dev.hddc.domains.hotdeal.application.ports.input.query

import dev.hddc.domains.hotdeal.domain.model.CandidateDealModel
import dev.hddc.domains.hotdeal.domain.model.CandidateDealStatus
import dev.hddc.framework.pagination.Pagination
import org.springframework.data.domain.Pageable

data class CandidateDealPageResult(
    val content: List<CandidateDealModel>,
    val pagination: Pagination,
)

interface CandidateDealAdminQueryUsecase {
    fun getAll(status: CandidateDealStatus, pageable: Pageable): CandidateDealPageResult
}
