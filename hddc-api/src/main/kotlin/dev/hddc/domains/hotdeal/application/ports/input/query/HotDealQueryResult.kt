package dev.hddc.domains.hotdeal.application.ports.input.query

import dev.hddc.domains.hotdeal.domain.model.HotDealCommentModel
import dev.hddc.domains.hotdeal.domain.model.HotDealModel
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealPageData
import dev.hddc.framework.pagination.Pagination

data class HotDealWithUserState(
    val deal: HotDealModel,
    val nickname: String,
    val dealNumber: Long,
    val isLiked: Boolean,
    val isVotedExpired: Boolean,
)

data class HotDealPageResult(
    val content: List<HotDealWithUserState>,
    val pagination: Pagination,
) {
    companion object {
        fun of(
            deals: HotDealPageData,
            nicknames: Map<Long, String>,
            likedIds: Set<Long>,
            votedExpiredIds: Set<Long>,
        ): HotDealPageResult = HotDealPageResult(
            content = deals.content.mapIndexed { index, deal ->
                HotDealWithUserState(
                    deal = deal,
                    nickname = nicknames[deal.userId] ?: "알 수 없음",
                    dealNumber = deals.pagination.totalItems - ((deals.pagination.currentPage - 1).toLong() * deals.pagination.perPage) - index,
                    isLiked = deal.id in likedIds,
                    isVotedExpired = deal.id in votedExpiredIds,
                )
            },
            pagination = deals.pagination,
        )
    }
}

data class HotDealWithNickname(
    val deal: HotDealModel,
    val nickname: String,
    val dealNumber: Long = 0,
)

data class AdminHotDealPageResult(
    val content: List<HotDealWithNickname>,
    val pagination: Pagination,
) {
    companion object {
        fun of(deals: HotDealPageData, nicknames: Map<Long, String>): AdminHotDealPageResult =
            AdminHotDealPageResult(
                content = deals.content.mapIndexed { index, deal ->
                    HotDealWithNickname(
                        deal = deal,
                        nickname = nicknames[deal.userId] ?: "알 수 없음",
                        dealNumber = deals.pagination.totalItems - ((deals.pagination.currentPage - 1).toLong() * deals.pagination.perPage) - index,
                    )
                },
                pagination = deals.pagination,
            )
    }
}

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
