package dev.hddc.domains.hotdeal.application.ports.input.query

import dev.hddc.domains.hotdeal.domain.model.HotDealModel

data class HotDealWithUserState(
    val deal: HotDealModel,
    val isLiked: Boolean,
    val isVotedExpired: Boolean,
)

data class HotDealPageResult(
    val content: List<HotDealWithUserState>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
)
