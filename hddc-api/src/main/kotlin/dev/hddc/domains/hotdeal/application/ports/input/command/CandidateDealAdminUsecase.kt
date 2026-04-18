package dev.hddc.domains.hotdeal.application.ports.input.command

data class BulkApproveResult(
    val succeeded: List<Long>,
    val failed: List<Long>,
)

data class BulkRejectResult(
    val succeeded: List<Long>,
    val failed: List<Long>,
)

interface CandidateDealAdminUsecase {
    fun approve(id: Long): Long
    fun reject(id: Long)
    fun bulkApprove(ids: List<Long>): BulkApproveResult
    fun bulkReject(ids: List<Long>): BulkRejectResult
    fun registerWithModifications(id: Long, command: RegisterCommand): Long

    data class RegisterCommand(
        val title: String,
        val url: String,
        val imageUrl: String?,
        val originalPrice: Int?,
        val dealPrice: Int?,
        val store: String?,
        val category: String?,
        val description: String?,
        val discountRate: Int?,
    )
}
