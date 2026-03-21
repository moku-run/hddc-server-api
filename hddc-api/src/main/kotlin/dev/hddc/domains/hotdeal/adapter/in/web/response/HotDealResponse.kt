package dev.hddc.domains.hotdeal.adapter.`in`.web.response

import dev.hddc.domains.hotdeal.domain.model.HotDealCommentModel
import dev.hddc.domains.hotdeal.domain.model.HotDealModel

data class HotDealResponse(
    val id: Long,
    val userId: Long,
    val title: String,
    val description: String?,
    val url: String,
    val imageUrl: String?,
    val originalPrice: Int?,
    val dealPrice: Int?,
    val discountRate: Int?,
    val category: String?,
    val store: String?,
    val likeCount: Int,
    val commentCount: Int,
    val expiredVoteCount: Int,
    val isExpired: Boolean,
    val isLiked: Boolean,
    val isVotedExpired: Boolean,
    val createdAt: String,
) {
    companion object {
        fun from(model: HotDealModel, isLiked: Boolean = false, isVotedExpired: Boolean = false) = HotDealResponse(
            id = model.id!!,
            userId = model.userId,
            title = model.title,
            description = model.description,
            url = model.url,
            imageUrl = model.imageUrl,
            originalPrice = model.originalPrice,
            dealPrice = model.dealPrice,
            discountRate = model.discountRate,
            category = model.category,
            store = model.store,
            likeCount = model.likeCount,
            commentCount = model.commentCount,
            expiredVoteCount = model.expiredVoteCount,
            isExpired = model.isExpired,
            isLiked = isLiked,
            isVotedExpired = isVotedExpired,
            createdAt = model.createdAt.toString(),
        )
    }
}

data class HotDealPageResponse(
    val content: List<HotDealResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
)

data class CommentResponse(
    val id: Long,
    val dealId: Long,
    val userId: Long,
    val nickname: String,
    val parentId: Long?,
    val content: String,
    val createdAt: String,
) {
    companion object {
        fun from(model: HotDealCommentModel, nickname: String) = CommentResponse(
            id = model.id!!,
            dealId = model.dealId,
            userId = model.userId,
            nickname = nickname,
            parentId = model.parentId,
            content = model.content,
            createdAt = model.createdAt.toString(),
        )
    }
}
