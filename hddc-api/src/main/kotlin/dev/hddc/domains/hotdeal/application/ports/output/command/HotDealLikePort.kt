package dev.hddc.domains.hotdeal.application.ports.output.command

import dev.hddc.domains.hotdeal.domain.model.HotDealLikeModel

interface HotDealLikePort {
    fun existsByDealIdAndUserId(dealId: Long, userId: Long): Boolean
    fun findByDealIdAndUserId(dealId: Long, userId: Long): HotDealLikeModel?
    fun save(model: HotDealLikeModel): HotDealLikeModel
    fun delete(model: HotDealLikeModel)
    fun findAllByUserIdAndDealIds(userId: Long, dealIds: List<Long>): List<HotDealLikeModel>
}
