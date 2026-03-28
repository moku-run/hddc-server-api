package dev.hddc.domains.hotdeal.adapter.`in`.web.response

import dev.hddc.domains.hotdeal.application.ports.input.query.CandidateDealPageData
import dev.hddc.domains.hotdeal.domain.model.CandidateDealModel

data class CandidateDealResponse(
    val id: Long,
    val sourceSite: String,
    val sourceId: String?,
    val title: String?,
    val description: String?,
    val postUrl: String,
    val dealLink: String?,
    val imageUrl: String?,
    val originalPrice: Int?,
    val dealPrice: Int?,
    val discountRate: Int?,
    val store: String?,
    val category: String?,
    val status: String,
    val crawledAt: String,
    val transferredAt: String?,
) {
    companion object {
        fun from(model: CandidateDealModel) = CandidateDealResponse(
            id = model.id!!,
            sourceSite = model.sourceSite,
            sourceId = model.sourceId,
            title = model.title,
            description = model.description,
            postUrl = model.postUrl,
            dealLink = model.dealLink,
            imageUrl = model.imageUrl,
            originalPrice = model.originalPrice,
            dealPrice = model.dealPrice,
            discountRate = model.discountRate,
            store = model.store,
            category = model.category,
            status = model.status.value,
            crawledAt = model.crawledAt.toString(),
            transferredAt = model.transferredAt?.toString(),
        )
    }
}

data class CandidateDealPageResponse(
    val content: List<CandidateDealResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
) {
    companion object {
        fun from(data: CandidateDealPageData) = CandidateDealPageResponse(
            content = data.content.map { CandidateDealResponse.from(it) },
            page = data.page,
            size = data.size,
            totalElements = data.totalElements,
            totalPages = data.totalPages,
        )
    }
}
