package dev.hddc.domains.hotdeal.domain.model

import java.time.Instant

data class CandidateDealModel(
    val id: Long? = null,
    val sourceSite: String,
    val sourceId: String? = null,
    val title: String? = null,
    val description: String? = null,
    val postUrl: String,
    val dealLink: String? = null,
    val imageUrl: String? = null,
    val originalPrice: Int? = null,
    val dealPrice: Int? = null,
    val discountRate: Int? = null,
    val store: String? = null,
    val category: String? = null,
    val status: String = CandidateDealStatus.PENDING.value,
    val crawledAt: Instant = Instant.now(),
    val transferredAt: Instant? = null,
)
