package dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.entity

import dev.hddc.framework.jpa.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "his_hot_deal_click")
class HotDealClickEntity(
    @Column(name = "deal_id", nullable = false)
    val dealId: Long,

    @Column(name = "user_id")
    val userId: Long? = null,

    @Column(nullable = false, length = 45)
    val ip: String,
) : BaseEntity()
