package dev.hddc.domains.hotdeal.adapter.`in`.web.response

import dev.hddc.domains.hotdeal.domain.model.HotDealModel
import dev.hddc.framework.pagination.Pagination

data class HotDealAdminResponse(
    val dealNumber: Long,
    val id: Long,
    val userId: Long,
    val nickname: String,
    val title: String,
    val description: String?,
    val originalUrl: String,
    val redirectUrl: String,
    val imageUrl: String?,
    val originalPrice: Int?,
    val dealPrice: Int?,
    val discountRate: Int?,
    val category: String?,
    val store: String?,
    val likeCount: Int,
    val commentCount: Int,
    val expiredVoteCount: Int,
    val clickCount: Int,
    val isExpired: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
) {
    companion object {
        fun from(model: HotDealModel, nickname: String, dealNumber: Long = 0) = HotDealAdminResponse(
            dealNumber = dealNumber,
            id = model.id,
            userId = model.userId,
            nickname = nickname,
            title = model.title,
            description = model.description,
            originalUrl = model.url,
            redirectUrl = "/r/deals/${model.id}",
            imageUrl = model.imageUrl,
            originalPrice = model.originalPrice,
            dealPrice = model.dealPrice,
            discountRate = model.discountRate,
            category = model.category,
            store = model.store,
            likeCount = model.likeCount,
            commentCount = model.commentCount,
            expiredVoteCount = model.expiredVoteCount,
            clickCount = model.clickCount,
            isExpired = model.isExpired,
            isDeleted = model.isDeleted,
            createdAt = model.createdAt.toString(),
        )
    }
}

data class HotDealAdminPageResponse(
    val content: List<HotDealAdminResponse>,
    val pagination: Pagination,
)
