package dev.hddc.domains.hotdeal.adapter.out.persistence

import dev.hddc.framework.jpa.BaseAuditEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "mst_hot_deal")
class HotDealEntity(
    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(nullable = false, length = 200)
    var title: String,

    @Column(length = 2000)
    var description: String? = null,

    @Column(nullable = false, length = 1000)
    var url: String,

    @Column(name = "image_url", length = 1000)
    var imageUrl: String? = null,

    @Column(name = "original_price")
    var originalPrice: Int? = null,

    @Column(name = "deal_price")
    var dealPrice: Int? = null,

    @Column(name = "discount_rate")
    var discountRate: Int? = null,

    @Column(length = 50)
    var category: String? = null,

    @Column(length = 100)
    var store: String? = null,

    @Column(name = "like_count", nullable = false)
    var likeCount: Int = 0,

    @Column(name = "comment_count", nullable = false)
    var commentCount: Int = 0,

    @Column(name = "expired_vote_count", nullable = false)
    var expiredVoteCount: Int = 0,

    @Column(name = "click_count", nullable = false)
    var clickCount: Int = 0,

    @Column(name = "is_expired", nullable = false)
    var isExpired: Boolean = false,
) : BaseAuditEntity() {
    fun softDelete() {
        isDeleted = true
        deletedAt = Instant.now()
    }
}
