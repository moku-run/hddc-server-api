package dev.hddc.domains.hotdeal.adapter.out.validator

import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.repository.CandidateDealRepository
import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.repository.loadById
import dev.hddc.domains.hotdeal.application.ports.output.validator.CandidateDealValidator
import dev.hddc.domains.hotdeal.domain.model.CandidateDealStatus
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.api.response.BusinessException
import org.springframework.stereotype.Component

@Component
class CandidateDealValidationAdapter(
    private val candidateDealRepository: CandidateDealRepository,
) : CandidateDealValidator {

    override fun validatePending(id: Long) {
        val entity = candidateDealRepository.loadById(id)
        if (entity.status != CandidateDealStatus.PENDING.name) {
            throw BusinessException(ApiResponseCode.CANDIDATE_DEAL_INVALID_STATUS)
        }
    }
}
