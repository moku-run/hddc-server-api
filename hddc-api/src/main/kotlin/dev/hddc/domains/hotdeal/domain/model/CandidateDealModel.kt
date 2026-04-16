package dev.hddc.domains.hotdeal.domain.model

import java.time.Instant

data class CandidateDealModel(
    val id: Long,
    val userId: Long,
    val title: String?,
    val url: String?,
    val imageUrl: String?,
    val originalPrice: Int?,
    val dealPrice: Int?,
    val store: String?,
    val category: String?,
    val status: CandidateDealStatus,
    val createdAt: Instant,
    val updatedAt: Instant,
)
