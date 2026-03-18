package dev.hddc.domains.hotdeal.domain.model

import java.time.Instant

data class HotDealCommentModel(
    val id: Long? = null,
    val dealId: Long,
    val userId: Long,
    val parentId: Long? = null,
    val content: String,
    val isDeleted: Boolean = false,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
)
