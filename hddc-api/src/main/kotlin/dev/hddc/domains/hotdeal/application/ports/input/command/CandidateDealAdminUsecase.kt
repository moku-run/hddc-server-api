package dev.hddc.domains.hotdeal.application.ports.input.command

data class ApproveResult(val approvedCount: Int)

interface CandidateDealAdminUsecase {
    fun approve(candidateDealId: Long): Long
    fun reject(candidateDealId: Long)
    fun bulkApprove(candidateDealIds: List<Long>): ApproveResult
}
