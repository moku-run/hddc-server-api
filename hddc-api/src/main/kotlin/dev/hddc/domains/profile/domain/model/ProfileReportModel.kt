package dev.hddc.domains.profile.domain.model

import java.time.Instant

data class ProfileReportModel(
    val id: Long? = null,
    val profileId: Long,
    val userId: Long,
    val reason: String,
    val createdAt: Instant = Instant.now(),
)

data class ProfileLinkReportModel(
    val id: Long? = null,
    val linkId: Long,
    val userId: Long,
    val reason: String,
    val createdAt: Instant = Instant.now(),
)
