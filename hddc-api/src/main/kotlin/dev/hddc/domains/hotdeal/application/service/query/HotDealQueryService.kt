package dev.hddc.domains.hotdeal.application.service.query

import dev.hddc.domains.hotdeal.application.ports.input.query.CommentCursorResult
import dev.hddc.domains.hotdeal.application.ports.input.query.HotDealPageResult
import dev.hddc.domains.hotdeal.application.ports.input.query.HotDealQueryUsecase
import dev.hddc.domains.hotdeal.application.ports.input.query.HotDealWithUserState
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommentPort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealExpiredVotePort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealLikePort
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealQueryPort
import dev.hddc.domains.hotdeal.domain.model.HotDealModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HotDealQueryService(
    private val hotDealQueryPort: HotDealQueryPort,
    private val hotDealLikePort: HotDealLikePort,
    private val hotDealExpiredVotePort: HotDealExpiredVotePort,
    private val hotDealCommentPort: HotDealCommentPort,
) : HotDealQueryUsecase {

    @Transactional(readOnly = true)
    override fun getDeals(userId: Long?, sort: String, page: Int, size: Int): HotDealPageResult {
        val pageable = PageRequest.of(page, size, resolveSort(sort))
        val dealPage = hotDealQueryPort.findActive(pageable)
        return toPageResult(dealPage, userId)
    }

    @Transactional(readOnly = true)
    override fun search(userId: Long?, query: String, page: Int, size: Int): HotDealPageResult {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val dealPage = hotDealQueryPort.search(query, pageable)
        return toPageResult(dealPage, userId)
    }

    @Transactional(readOnly = true)
    override fun getComments(dealId: Long, afterId: Long?, size: Int): CommentCursorResult {
        // size+1개 조회하여 다음 페이지 존재 여부 판단
        val rootComments = hotDealCommentPort.findRootComments(dealId, afterId, size + 1)
        val hasNext = rootComments.size > size
        val pagedRoots = if (hasNext) rootComments.take(size) else rootComments

        // 루트 댓글들의 대댓글 일괄 조회
        val rootIds = pagedRoots.mapNotNull { it.id }
        val replies = hotDealCommentPort.findRepliesByParentIds(rootIds)

        // 전체 댓글(루트+대댓글) 중 답글이 존재하는 부모 ID 집합
        val allComments = pagedRoots + replies
        val parentIdsWithReplies = allComments
            .filter { it.parentId != null }
            .map { it.parentId!! }
            .toSet()

        // 삭제된 댓글 필터: 답글 있으면 유지("[삭제된 메시지입니다.]"), 없으면 제외
        val filteredRoots = pagedRoots.filter { root ->
            !root.isDeleted || root.id in parentIdsWithReplies
        }
        val filteredReplies = replies.filter { reply ->
            !reply.isDeleted || reply.id in parentIdsWithReplies
        }

        // 루트 + 대댓글을 flat list로 합산 (루트 순서 유지, 각 루트 아래 대댓글 배치)
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

    private fun toPageResult(dealPage: Page<HotDealModel>, userId: Long?): HotDealPageResult {
        val dealIds = dealPage.content.map { it.id!! }

        val likedIds = userId?.let { uid ->
            hotDealLikePort.findAllByUserIdAndDealIds(uid, dealIds).map { it.dealId }.toSet()
        } ?: emptySet()

        val votedExpiredIds = userId?.let { uid ->
            hotDealExpiredVotePort.findAllByUserIdAndDealIds(uid, dealIds).map { it.dealId }.toSet()
        } ?: emptySet()

        val content = dealPage.content.map { deal ->
            HotDealWithUserState(
                deal = deal,
                isLiked = deal.id!! in likedIds,
                isVotedExpired = deal.id!! in votedExpiredIds,
            )
        }

        return HotDealPageResult(
            content = content,
            page = dealPage.number,
            size = dealPage.size,
            totalElements = dealPage.totalElements,
            totalPages = dealPage.totalPages,
        )
    }

    private fun resolveSort(sort: String): Sort = when (sort) {
        "popular" -> Sort.by(Sort.Direction.DESC, "likeCount").and(Sort.by(Sort.Direction.DESC, "createdAt"))
        "discount" -> Sort.by(Sort.Direction.DESC, "discountRate").and(Sort.by(Sort.Direction.DESC, "createdAt"))
        else -> Sort.by(Sort.Direction.DESC, "createdAt")
    }
}
