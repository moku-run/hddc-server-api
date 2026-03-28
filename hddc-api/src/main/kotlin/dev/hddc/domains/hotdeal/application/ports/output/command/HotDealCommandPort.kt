package dev.hddc.domains.hotdeal.application.ports.output.command

import dev.hddc.domains.hotdeal.domain.model.CreateHotDealModel
import dev.hddc.domains.hotdeal.domain.model.HotDealModel

interface HotDealCommandPort {
    fun create(model: CreateHotDealModel): HotDealModel
    fun updateLikeCount(dealId: Long, count: Int)
    fun updateCommentCount(dealId: Long, count: Int)
    fun updateClickCount(dealId: Long, count: Int)
    fun updateExpiredVote(dealId: Long, count: Int, expired: Boolean)
    fun softDelete(dealId: Long)
    fun update(dealId: Long, updater: (HotDealModel) -> HotDealModel): HotDealModel
}
