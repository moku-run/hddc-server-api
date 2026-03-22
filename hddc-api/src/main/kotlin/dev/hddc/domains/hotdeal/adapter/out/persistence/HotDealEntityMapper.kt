package dev.hddc.domains.hotdeal.adapter.out.persistence

import dev.hddc.domains.hotdeal.domain.model.HotDealCommentModel
import dev.hddc.domains.hotdeal.domain.model.HotDealModel

fun HotDealEntity.toDomain(): HotDealModel = HotDealModel(
    id = id,
    userId = userId,
    title = title,
    description = description,
    url = url,
    imageUrl = imageUrl,
    originalPrice = originalPrice,
    dealPrice = dealPrice,
    discountRate = discountRate,
    category = category,
    store = store,
    likeCount = likeCount,
    commentCount = commentCount,
    expiredVoteCount = expiredVoteCount,
    clickCount = clickCount,
    isExpired = isExpired,
    isDeleted = isDeleted,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun HotDealCommentEntity.toDomain(): HotDealCommentModel = HotDealCommentModel(
    id = id,
    dealId = dealId,
    userId = userId,
    parentId = parentId,
    content = content,
    likeCount = likeCount,
    isDeleted = isDeleted,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
