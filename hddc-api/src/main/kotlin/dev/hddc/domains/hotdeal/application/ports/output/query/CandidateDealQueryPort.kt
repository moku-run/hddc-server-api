package dev.hddc.domains.hotdeal.application.ports.output.query

import dev.hddc.domains.hotdeal.domain.model.CandidateDealModel
import dev.hddc.framework.pagination.Pagination

data class CandidateDealPageData(
    val content: List<CandidateDealModel>,
    val pagination: Pagination,
)

interface CandidateDealQueryPort {
    fun findByStatus(status: String, page: Int, size: Int): CandidateDealPageData
    fun findById(id: Long): CandidateDealModel?
    fun findAllByIdsAndStatus(ids: List<Long>, status: String): List<CandidateDealModel>
    fun loadById(id: Long): CandidateDealModel
}
