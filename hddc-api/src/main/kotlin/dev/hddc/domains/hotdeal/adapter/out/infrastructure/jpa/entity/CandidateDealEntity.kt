package dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "crawl_hot_deal")
class CandidateDealEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "source_site", nullable = false, length = 50)
    val sourceSite: String,

    @Column(name = "source_id", length = 100)
    val sourceId: String? = null,

    @Column(length = 500)
    var title: String? = null,

    @Column(length = 2000)
    var description: String? = null,

    @Column(name = "post_url", nullable = false, length = 1000)
    val postUrl: String,

    @Column(name = "deal_link", length = 1000)
    var dealLink: String? = null,

    @Column(name = "image_url", length = 1000)
    var imageUrl: String? = null,

    @Column(name = "original_price")
    var originalPrice: Int? = null,

    @Column(name = "deal_price")
    var dealPrice: Int? = null,

    @Column(name = "discount_rate")
    var discountRate: Int? = null,

    @Column(length = 100)
    var store: String? = null,

    @Column(length = 50)
    var category: String? = null,

    @Column(nullable = false, length = 20)
    var status: String = "PENDING",

    @Column(name = "crawled_at", nullable = false)
    val crawledAt: Instant = Instant.now(),

    @Column(name = "transferred_at")
    var transferredAt: Instant? = null,
)
