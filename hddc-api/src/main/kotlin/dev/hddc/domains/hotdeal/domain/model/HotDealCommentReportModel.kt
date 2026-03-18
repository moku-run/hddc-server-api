package dev.hddc.domains.hotdeal.domain.model

import java.time.Instant

data class HotDealCommentReportModel(
    val id: Long? = null,
    val commentId: Long,
    val userId: Long,
    val reason: String,
    val createdAt: Instant = Instant.now(),
)
