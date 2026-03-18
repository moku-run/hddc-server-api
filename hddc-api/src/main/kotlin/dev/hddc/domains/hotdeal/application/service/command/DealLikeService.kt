package dev.hddc.domains.hotdeal.application.service.command

import dev.hddc.domains.hotdeal.application.ports.input.command.DealLikeUsecase
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommandPort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealLikePort
import dev.hddc.domains.hotdeal.domain.model.HotDealLikeModel
import dev.hddc.framework.api.response.ApiResponseCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DealLikeService(
    private val hotDealCommandPort: HotDealCommandPort,
    private val hotDealLikePort: HotDealLikePort,
) : DealLikeUsecase {

    @Transactional
    override fun like(userId: Long, dealId: Long) {
        val deal = hotDealCommandPort.findById(dealId)
            ?: throw IllegalArgumentException(ApiResponseCode.HOT_DEAL_NOT_FOUND.code)
        if (hotDealLikePort.existsByDealIdAndUserId(dealId, userId)) return

        hotDealLikePort.save(HotDealLikeModel(dealId = dealId, userId = userId))
        hotDealCommandPort.save(deal.copy(likeCount = deal.likeCount + 1))
    }

    @Transactional
    override fun unlike(userId: Long, dealId: Long) {
        val deal = hotDealCommandPort.findById(dealId)
            ?: throw IllegalArgumentException(ApiResponseCode.HOT_DEAL_NOT_FOUND.code)
        val like = hotDealLikePort.findByDealIdAndUserId(dealId, userId) ?: return

        hotDealLikePort.delete(like)
        hotDealCommandPort.save(deal.copy(likeCount = maxOf(0, deal.likeCount - 1)))
    }
}
