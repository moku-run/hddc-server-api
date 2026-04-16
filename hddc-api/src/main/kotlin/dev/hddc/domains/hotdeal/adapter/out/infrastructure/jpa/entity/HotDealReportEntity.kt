package dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.entity

import dev.hddc.framework.jpa.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "hot_deal_report")
class HotDealReportEntity(
    @Column(name = "deal_id", nullable = false)
    val dealId: Long,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(nullable = false, length = 100)
    val reason: String,
) : BaseEntity()
