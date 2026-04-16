package dev.hddc.domains.hotdeal.application.ports.output.query

import dev.hddc.domains.hotdeal.domain.model.CandidateDealModel
import dev.hddc.domains.hotdeal.domain.model.CandidateDealStatus
import dev.hddc.framework.pagination.Pagination
import org.springframework.data.domain.Pageable

interface CandidateDealQueryPort {
    fun findById(id: Long): CandidateDealModel?
    fun loadById(id: Long): CandidateDealModel
    fun findByStatus(status: CandidateDealStatus, pageable: Pageable): CandidateDealPageData
}

data class CandidateDealPageData(
    val content: List<CandidateDealModel>,
    val pagination: Pagination,
)
