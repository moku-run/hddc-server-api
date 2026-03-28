package dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.entity

import dev.hddc.framework.jpa.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "his_hot_deal_comment_like")
class HotDealCommentLikeEntity(
    @Column(name = "comment_id", nullable = false)
    val commentId: Long,

    @Column(name = "user_id", nullable = false)
    val userId: Long,
) : BaseEntity()
