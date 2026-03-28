package dev.hddc.domains.hotdeal.application.ports.output.command

import dev.hddc.domains.hotdeal.domain.model.CandidateDealModel

data class CandidateDealPageData(
    val content: List<CandidateDealModel>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
)

interface CandidateDealPort {
    fun findByStatus(status: String, page: Int, size: Int): CandidateDealPageData
    fun findById(id: Long): CandidateDealModel?
    fun findAllByIdsAndStatus(ids: List<Long>, status: String): List<CandidateDealModel>
    fun updateStatus(id: Long, status: String, transferredAt: java.time.Instant? = null)
}
