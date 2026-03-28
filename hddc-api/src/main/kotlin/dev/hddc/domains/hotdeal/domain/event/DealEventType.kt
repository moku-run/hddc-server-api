package dev.hddc.domains.hotdeal.domain.event

enum class DealEventType(val value: String) {
    NEW_DEAL("new-deal"),
    DEAL_UPDATED("deal-updated"),
    DEAL_EXPIRED("deal-expired"),
    DEAL_DELETED("deal-deleted"),
    NEW_COMMENT("new-comment"),
    COMMENT_DELETED("comment-deleted"),
}
