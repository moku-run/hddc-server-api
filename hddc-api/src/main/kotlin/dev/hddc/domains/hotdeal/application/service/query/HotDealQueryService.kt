package dev.hddc.domains.hotdeal.application.service.query

import dev.hddc.domains.hotdeal.application.ports.input.query.CommentCursorResult
import dev.hddc.domains.hotdeal.application.ports.input.query.CommentWithNickname
import dev.hddc.domains.hotdeal.application.ports.input.query.EnrichedCommentCursorResult
import dev.hddc.domains.hotdeal.application.ports.input.query.HotDealPageResult
import dev.hddc.domains.hotdeal.application.ports.input.query.HotDealQueryUsecase
import dev.hddc.domains.hotdeal.application.ports.input.query.HotDealWithUserState
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommentLikePort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommentPort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealExpiredVotePort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealLikePort
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealPageData
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealQueryPort
import dev.hddc.domains.user.application.ports.output.query.UserQueryPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HotDealQueryService(
    private val hotDealQueryPort: HotDealQueryPort,
    private val hotDealLikePort: HotDealLikePort,
    private val hotDealExpiredVotePort: HotDealExpiredVotePort,
    private val hotDealCommentPort: HotDealCommentPort,
    private val hotDealCommentLikePort: HotDealCommentLikePort,
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
        val rootComments = hotDealCommentPort.findRootComments(dealId, afterId, size + 1)
        val hasNext = rootComments.size > size
        val pagedRoots = if (hasNext) rootComments.take(size) else rootComments

        val rootIds = pagedRoots.map { it.id }
        val replies = hotDealCommentPort.findRepliesByParentIds(rootIds)

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

        return CommentCursorResult(
            comments = comments,
            nextCursor = nextCursor,
            hasNext = hasNext,
        )
    }

    @Transactional(readOnly = true)
    override fun getCommentsEnriched(dealId: Long, userId: Long?, afterId: Long?, size: Int): EnrichedCommentCursorResult {
        val result = getComments(dealId, afterId, size)
        val comments = result.comments

        val userIds = comments.map { it.userId }.distinct()
        val nicknames = userQueryPort.findNicknamesByIds(userIds)

        val likedCommentIds = if (userId != null) {
            val commentIds = comments.filter { !it.isDeleted }.map { it.id }
            if (commentIds.isNotEmpty()) {
                hotDealCommentLikePort.findAllByUserIdAndCommentIds(userId, commentIds)
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
            nextCursor = result.nextCursor,
            hasNext = result.hasNext,
        )
    }

    private fun toPageResult(dealPage: HotDealPageData, userId: Long?): HotDealPageResult {
        val dealIds = dealPage.content.map { it.id }
        val userIds = dealPage.content.map { it.userId }.distinct()
        val nicknames = userQueryPort.findNicknamesByIds(userIds)

        val likedIds = userId?.let { uid ->
            hotDealLikePort.findAllByUserIdAndDealIds(uid, dealIds).map { it.dealId }.toSet()
        } ?: emptySet()

        val votedExpiredIds = userId?.let { uid ->
            hotDealExpiredVotePort.findAllByUserIdAndDealIds(uid, dealIds).map { it.dealId }.toSet()
        } ?: emptySet()

        val content = dealPage.content.map { deal ->
            HotDealWithUserState(
                deal = deal,
                nickname = nicknames[deal.userId] ?: "알 수 없음",
                isLiked = deal.id in likedIds,
                isVotedExpired = deal.id in votedExpiredIds,
            )
        }

        return HotDealPageResult(
            content = content,
            page = dealPage.page,
            size = dealPage.size,
            totalElements = dealPage.totalElements,
            totalPages = dealPage.totalPages,
        )
    }
}
