package dev.hddc.domains.hotdeal.application.service.query

import dev.hddc.domains.hotdeal.adapter.`in`.web.response.HotDealPageResponse
import dev.hddc.domains.hotdeal.adapter.`in`.web.response.HotDealResponse
import dev.hddc.domains.hotdeal.application.ports.input.query.HotDealQueryUsecase
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommentPort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealExpiredVotePort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealLikePort
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealQueryPort
import dev.hddc.domains.hotdeal.domain.model.HotDealCommentModel
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
    override fun getDeals(userId: Long?, sort: String, page: Int, size: Int): HotDealPageResponse {
        val pageable = PageRequest.of(page, size, resolveSort(sort))
        val dealPage = hotDealQueryPort.findActive(pageable)
        return toPageResponse(dealPage, userId)
    }

    @Transactional(readOnly = true)
    override fun search(userId: Long?, query: String, page: Int, size: Int): HotDealPageResponse {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val dealPage = hotDealQueryPort.search(query, pageable)
        return toPageResponse(dealPage, userId)
    }

    @Transactional(readOnly = true)
    override fun getComments(dealId: Long): List<HotDealCommentModel> =
        hotDealCommentPort.findAllByDealId(dealId)

    private fun toPageResponse(dealPage: Page<HotDealModel>, userId: Long?): HotDealPageResponse {
        val dealIds = dealPage.content.map { it.id!! }

        val likedIds = userId?.let { uid ->
            hotDealLikePort.findAllByUserIdAndDealIds(uid, dealIds).map { it.dealId }.toSet()
        } ?: emptySet()

        val votedExpiredIds = userId?.let { uid ->
            hotDealExpiredVotePort.findAllByUserIdAndDealIds(uid, dealIds).map { it.dealId }.toSet()
        } ?: emptySet()

        val content = dealPage.content.map { deal ->
            HotDealResponse.from(
                model = deal,
                isLiked = deal.id!! in likedIds,
                isVotedExpired = deal.id!! in votedExpiredIds,
            )
        }

        return HotDealPageResponse(
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
