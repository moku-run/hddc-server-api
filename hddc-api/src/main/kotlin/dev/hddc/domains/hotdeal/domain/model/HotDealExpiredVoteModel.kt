package dev.hddc.domains.hotdeal.domain.model

import java.time.Instant

data class HotDealExpiredVoteModel(
    val id: Long? = null,
    val dealId: Long,
    val userId: Long,
    val createdAt: Instant = Instant.now(),
)
