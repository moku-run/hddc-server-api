package dev.hddc.domains.hotdeal.application.ports.input.query

import dev.hddc.domains.hotdeal.domain.model.HotDealCommentModel
import dev.hddc.domains.hotdeal.domain.model.HotDealModel

data class HotDealWithUserState(
    val deal: HotDealModel,
    val nickname: String,
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

data class HotDealWithNickname(
    val deal: HotDealModel,
    val nickname: String,
)

data class AdminHotDealPageResult(
    val content: List<HotDealWithNickname>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
)

data class CommentCursorResult(
    val comments: List<HotDealCommentModel>,
    val nextCursor: Long?,
    val hasNext: Boolean,
)

data class CommentWithNickname(
    val comment: HotDealCommentModel,
    val nickname: String,
    val isLiked: Boolean,
)

data class EnrichedCommentCursorResult(
    val comments: List<CommentWithNickname>,
    val nextCursor: Long?,
    val hasNext: Boolean,
)
