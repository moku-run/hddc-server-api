package dev.hddc.domains.hotdeal.adapter.`in`.web.response

import dev.hddc.domains.hotdeal.application.ports.input.command.BulkApproveResult
import dev.hddc.domains.hotdeal.application.ports.input.command.BulkRejectResult
import dev.hddc.domains.hotdeal.application.ports.input.query.CandidateDealPageResult
import dev.hddc.domains.hotdeal.domain.model.CandidateDealModel
import dev.hddc.framework.pagination.Pagination

data class CandidateDealResponse(
    val id: Long,
    val userId: Long,
    val title: String?,
    val url: String?,
    val imageUrl: String?,
    val originalPrice: Int?,
    val dealPrice: Int?,
    val store: String?,
    val category: String?,
    val status: String,
    val createdAt: String,
    val updatedAt: String,
) {
    companion object {
        fun from(model: CandidateDealModel) = CandidateDealResponse(
            id = model.id,
            userId = model.userId,
            title = model.title,
            url = model.url,
            imageUrl = model.imageUrl,
            originalPrice = model.originalPrice,
            dealPrice = model.dealPrice,
            store = model.store,
            category = model.category,
            status = model.status.name,
            createdAt = model.createdAt.toString(),
            updatedAt = model.updatedAt.toString(),
        )
    }
}

data class CandidateDealPageResponse(
    val content: List<CandidateDealResponse>,
    val pagination: Pagination,
) {
    companion object {
        fun from(result: CandidateDealPageResult) = CandidateDealPageResponse(
            content = result.content.map { CandidateDealResponse.from(it) },
            pagination = result.pagination,
        )
    }
}

data class BulkApproveResponse(
    val succeeded: List<Long>,
    val failed: List<Long>,
) {
    companion object {
        fun from(result: BulkApproveResult) = BulkApproveResponse(
            succeeded = result.succeeded,
            failed = result.failed,
        )
    }
}

data class BulkRejectResponse(
    val succeeded: List<Long>,
    val failed: List<Long>,
) {
    companion object {
        fun from(result: BulkRejectResult) = BulkRejectResponse(
            succeeded = result.succeeded,
            failed = result.failed,
        )
    }
}
