package dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.entity

import dev.hddc.domains.hotdeal.domain.model.CandidateDealStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "candidate_hot_deal")
class CandidateDealEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(length = 500)
    var title: String? = null,

    @Column(length = 1000)
    var url: String? = null,

    @Column(name = "image_url", length = 1000)
    var imageUrl: String? = null,

    @Column(name = "original_price")
    var originalPrice: Int? = null,

    @Column(name = "deal_price")
    var dealPrice: Int? = null,

    @Column(length = 100)
    var store: String? = null,

    @Column(length = 50)
    var category: String? = null,

    @Column(nullable = false, length = 20)
    var status: String = CandidateDealStatus.PENDING.name,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),
)
