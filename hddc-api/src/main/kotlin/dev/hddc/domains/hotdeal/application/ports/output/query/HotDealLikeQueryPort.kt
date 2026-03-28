package dev.hddc.domains.hotdeal.application.ports.output.query

import dev.hddc.domains.hotdeal.domain.model.HotDealLikeModel

interface HotDealLikeQueryPort {
    fun existsByDealIdAndUserId(dealId: Long, userId: Long): Boolean
    fun findByDealIdAndUserId(dealId: Long, userId: Long): HotDealLikeModel?
    fun findAllByUserIdAndDealIds(userId: Long, dealIds: List<Long>): List<HotDealLikeModel>
}
