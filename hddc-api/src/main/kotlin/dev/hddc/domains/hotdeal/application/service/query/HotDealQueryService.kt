package dev.hddc.domains.hotdeal.application.service.query

import dev.hddc.domains.hotdeal.application.ports.input.query.CommentCursorResult
import dev.hddc.domains.hotdeal.application.ports.input.query.CommentWithNickname
import dev.hddc.domains.hotdeal.application.ports.input.query.EnrichedCommentCursorResult
import dev.hddc.domains.hotdeal.application.ports.input.query.HotDealPageResult
import dev.hddc.domains.hotdeal.application.ports.input.query.HotDealQueryUsecase
import dev.hddc.domains.hotdeal.application.ports.input.query.HotDealWithUserState
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealCommentLikeQueryPort
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealCommentQueryPort
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealExpiredVoteQueryPort
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealLikeQueryPort
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealPageData
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealQueryPort
import dev.hddc.domains.hotdeal.domain.model.HotDealCommentModel
import dev.hddc.domains.user.application.ports.output.query.UserQueryPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HotDealQueryService(
    private val hotDealQueryPort: HotDealQueryPort,
    private val hotDealLikeQueryPort: HotDealLikeQueryPort,
    private val hotDealExpiredVoteQueryPort: HotDealExpiredVoteQueryPort,
    private val hotDealCommentQueryPort: HotDealCommentQueryPort,
    private val hotDealCommentLikeQueryPort: HotDealCommentLikeQueryPort,
    private val userQueryPort: UserQueryPort,
) : HotDealQueryUsecase {

    @Transactional(readOnly = true)
    override fun getDeals(userId: Long?, sort: String, page: Int, size: Int): HotDealPageResult {
        val dealPage = hotDealQueryPort.findActive(sort, page, size)
        return toPageResult(dealPage, userId)
    }

    @Transactional(readOnly = true)
    override fun search(userId: Long?, query: String, page: Int, size: Int): HotDealPageResult {
        val dealPage = hotDealQueryPort.search(query, page, size)
        return toPageResult(dealPage, userId)
    }

    @Transactional(readOnly = true)
    override fun getComments(dealId: Long, afterId: Long?, size: Int): CommentCursorResult {
        val (comments, nextCursor, hasNext) = fetchComments(dealId, afterId, size)
        return CommentCursorResult(
            comments = comments,
            nextCursor = nextCursor,
            hasNext = hasNext,
        )
    }

    @Transactional(readOnly = true)
    override fun getCommentsEnriched(dealId: Long, userId: Long?, afterId: Long?, size: Int): EnrichedCommentCursorResult {
        val (comments, nextCursor, hasNext) = fetchComments(dealId, afterId, size)

        val userIds = comments.map { it.userId }.distinct()
        val nicknames = userQueryPort.findNicknamesByIds(userIds)

        val likedCommentIds = if (userId != null) {
            val commentIds = comments.filter { !it.isDeleted }.map { it.id }
            if (commentIds.isNotEmpty()) {
                hotDealCommentLikeQueryPort.findAllByUserIdAndCommentIds(userId, commentIds)
                    .map { it.commentId }.toSet()
            } else emptySet()
        } else {
            emptySet()
        }

        return EnrichedCommentCursorResult(
            comments = comments.map { comment ->
                CommentWithNickname(
                    comment = comment,
                    nickname = nicknames[comment.userId] ?: "알 수 없음",
                    isLiked = comment.id in likedCommentIds,
                )
            },
            nextCursor = nextCursor,
            hasNext = hasNext,
        )
    }

    private data class FetchCommentsResult(
        val comments: List<HotDealCommentModel>,
        val nextCursor: Long?,
        val hasNext: Boolean,
    )

    private fun fetchComments(dealId: Long, afterId: Long?, size: Int): FetchCommentsResult {
        val rootComments = hotDealCommentQueryPort.findRootComments(dealId, afterId, size + 1)
        val hasNext = rootComments.size > size
        val pagedRoots = if (hasNext) rootComments.take(size) else rootComments

        val rootIds = pagedRoots.map { it.id }
        val replies = hotDealCommentQueryPort.findRepliesByParentIds(rootIds)

        val allComments = pagedRoots + replies
        val parentIdsWithReplies = allComments
            .filter { it.parentId != null }
            .map { it.parentId!! }
            .toSet()

        val filteredRoots = pagedRoots.filter { !it.isDeleted || it.id in parentIdsWithReplies }
        val filteredReplies = replies.filter { !it.isDeleted || it.id in parentIdsWithReplies }

        val comments = filteredRoots.flatMap { root ->
            val rootReplies = filteredReplies.filter { it.parentId == root.id }
            listOf(root) + rootReplies
        }

        val nextCursor = if (hasNext) pagedRoots.last().id else null

        return FetchCommentsResult(
            comments = comments,
            nextCursor = nextCursor,
            hasNext = hasNext,
        )
    }

    private fun toPageResult(dealPage: HotDealPageData, userId: Long?): HotDealPageResult {
        val dealIds = dealPage.content.map { it.id }
        val userIds = dealPage.content.map { it.userId }.distinct()
        val nicknames = userQueryPort.findNicknamesByIds(userIds)

        val likedIds = userId?.let { uid ->
            hotDealLikeQueryPort.findAllByUserIdAndDealIds(uid, dealIds).map { it.dealId }.toSet()
        } ?: emptySet()

        val votedExpiredIds = userId?.let { uid ->
            hotDealExpiredVoteQueryPort.findAllByUserIdAndDealIds(uid, dealIds).map { it.dealId }.toSet()
        } ?: emptySet()

        val content = dealPage.content.mapIndexed { index, deal ->
            HotDealWithUserState(
                deal = deal,
                nickname = nicknames[deal.userId] ?: "알 수 없음",
                dealNumber = dealPage.pagination.totalItems - ((dealPage.pagination.currentPage - 1).toLong() * dealPage.pagination.perPage) - index,
                isLiked = deal.id in likedIds,
                isVotedExpired = deal.id in votedExpiredIds,
            )
        }

        return HotDealPageResult(
            content = content,
            pagination = dealPage.pagination,
        )
    }
}
