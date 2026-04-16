package dev.hddc.domains.hotdeal.application.ports.input.command

data class BulkApproveResult(
    val succeeded: List<Long>,
    val failed: List<Long>,
)

interface CandidateDealAdminUsecase {
    fun approve(id: Long): Long
    fun reject(id: Long)
    fun bulkApprove(ids: List<Long>): BulkApproveResult
}
