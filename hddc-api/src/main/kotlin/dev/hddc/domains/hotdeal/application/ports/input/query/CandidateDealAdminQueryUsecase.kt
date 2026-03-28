package dev.hddc.domains.hotdeal.application.ports.input.query

import dev.hddc.domains.hotdeal.application.ports.output.query.CandidateDealPageData
import org.springframework.data.domain.Pageable

interface CandidateDealAdminQueryUsecase {
    fun getCandidateDeals(status: String, pageable: Pageable): CandidateDealPageData
}
