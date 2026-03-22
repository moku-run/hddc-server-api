package dev.hddc.domains.hotdeal.adapter.out.persistence

import dev.hddc.framework.jpa.BaseAuditEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

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
) : BaseAuditEntity()
