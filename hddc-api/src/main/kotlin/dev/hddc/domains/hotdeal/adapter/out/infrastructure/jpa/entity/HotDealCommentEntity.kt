package dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.entity

import dev.hddc.framework.jpa.BaseAuditEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "mst_hot_deal_comment")
class HotDealCommentEntity(
    @Column(name = "deal_id", nullable = false)
    val dealId: Long,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "parent_id")
    val parentId: Long? = null,

    @Column(nullable = false, length = 1000)
    var content: String,

    @Column(name = "like_count", nullable = false)
    var likeCount: Int = 0,
) : BaseAuditEntity() {
    fun softDelete() {
        isDeleted = true
        deletedAt = Instant.now()
    }
}
