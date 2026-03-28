package dev.hddc.domains.hotdeal.application.ports.input.query

import dev.hddc.domains.hotdeal.domain.model.CandidateDealModel

data class CandidateDealPageData(
    val content: List<CandidateDealModel>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
)
