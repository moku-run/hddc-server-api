package dev.hddc.domains.hotdeal.domain.model

data class CreateHotDealCommentModel(
    val dealId: Long,
    val userId: Long,
    val parentId: Long? = null,
    val content: String,
)
