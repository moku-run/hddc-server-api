package dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.repository

import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.entity.CandidateDealEntity
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.api.response.BusinessException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface CandidateDealRepository : JpaRepository<CandidateDealEntity, Long> {
    fun findByStatus(status: String, pageable: Pageable): Page<CandidateDealEntity>
    fun findAllByIdInAndStatus(ids: List<Long>, status: String): List<CandidateDealEntity>
}

fun CandidateDealRepository.loadById(id: Long): CandidateDealEntity =
    findById(id).orElseThrow { BusinessException(ApiResponseCode.CANDIDATE_DEAL_NOT_FOUND) }
