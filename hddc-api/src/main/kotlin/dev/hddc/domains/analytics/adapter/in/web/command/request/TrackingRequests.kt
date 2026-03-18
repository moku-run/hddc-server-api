package dev.hddc.domains.analytics.adapter.`in`.web.command.request

import jakarta.validation.constraints.NotBlank

data class TrackClickRequest(
    @field:NotBlank val slug: String,
    val linkId: Long,
)

data class TrackViewRequest(
    @field:NotBlank val slug: String,
)
