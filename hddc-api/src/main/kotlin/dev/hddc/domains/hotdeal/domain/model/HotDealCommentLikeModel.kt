package dev.hddc.domains.hotdeal.domain.model

import java.time.Instant

data class HotDealCommentLikeModel(
    val id: Long? = null,
    val commentId: Long,
    val userId: Long,
    val createdAt: Instant = Instant.now(),
)
