package dev.hddc.domains.hotdeal.domain.spec

object HotDealSpec {
    const val EXPIRED_VOTE_THRESHOLD = 10
    const val SYSTEM_USER_ID = 1L
    const val CLICK_DEDUP_MINUTES = 10L

    fun isExpiredThresholdReached(voteCount: Int): Boolean = voteCount >= EXPIRED_VOTE_THRESHOLD
}
