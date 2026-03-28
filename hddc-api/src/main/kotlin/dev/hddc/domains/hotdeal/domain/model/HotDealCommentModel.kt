package dev.hddc.domains.hotdeal.domain.model

import java.time.Instant

data class HotDealCommentModel(
    val id: Long,
    val dealId: Long,
    val userId: Long,
    val parentId: Long?,
    val content: String,
    val likeCount: Int,
    val isDeleted: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant,
)
