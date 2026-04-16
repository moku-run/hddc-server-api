package dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.mapper

import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.entity.CandidateDealEntity
import dev.hddc.domains.hotdeal.domain.model.CandidateDealModel
import dev.hddc.domains.hotdeal.domain.model.CandidateDealStatus

fun CandidateDealEntity.toDomain(): CandidateDealModel = CandidateDealModel(
    id = requireNotNull(id) { "CandidateDealEntity id must not be null" },
    userId = userId,
    title = title,
    url = url,
    imageUrl = imageUrl,
    originalPrice = originalPrice,
    dealPrice = dealPrice,
    store = store,
    category = category,
    status = CandidateDealStatus.valueOf(status),
    createdAt = createdAt,
    updatedAt = updatedAt,
)
