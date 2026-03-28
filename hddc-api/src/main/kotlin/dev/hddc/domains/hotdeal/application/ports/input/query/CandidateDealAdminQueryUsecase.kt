package dev.hddc.domains.hotdeal.application.ports.input.query

import dev.hddc.domains.hotdeal.application.ports.output.query.CandidateDealPageData

interface CandidateDealAdminQueryUsecase {
    fun getCandidateDeals(status: String, page: Int, size: Int): CandidateDealPageData
}
