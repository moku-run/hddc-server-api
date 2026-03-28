package dev.hddc.domains.hotdeal.application.ports.input.command

import dev.hddc.domains.hotdeal.application.ports.output.command.CandidateDealPageData

data class ApproveResult(val approvedCount: Int)

interface CandidateDealAdminUsecase {
    fun getCandidateDeals(status: String, page: Int, size: Int): CandidateDealPageData
    fun approve(candidateDealId: Long): Long
    fun reject(candidateDealId: Long)
    fun bulkApprove(candidateDealIds: List<Long>): ApproveResult
}
