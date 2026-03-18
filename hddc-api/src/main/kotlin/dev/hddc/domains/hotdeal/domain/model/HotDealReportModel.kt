package dev.hddc.domains.hotdeal.domain.model

import java.time.Instant

data class HotDealReportModel(
    val id: Long? = null,
    val dealId: Long,
    val userId: Long,
    val reason: String,
    val createdAt: Instant = Instant.now(),
)
