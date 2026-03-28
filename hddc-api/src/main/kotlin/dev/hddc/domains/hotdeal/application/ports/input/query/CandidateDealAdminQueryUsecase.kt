package dev.hddc.domains.hotdeal.application.ports.input.query

interface CandidateDealAdminQueryUsecase {
    fun getCandidateDeals(status: String, page: Int, size: Int): CandidateDealPageData
}
