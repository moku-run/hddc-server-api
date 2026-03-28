package dev.hddc.domains.hotdeal.adapter.out.validator

import dev.hddc.domains.hotdeal.application.ports.output.query.CandidateDealQueryPort
import dev.hddc.domains.hotdeal.application.ports.output.validator.CandidateDealValidator
import dev.hddc.domains.hotdeal.domain.model.CandidateDealStatus
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.api.response.BusinessException
import org.springframework.stereotype.Component

@Component
class CandidateDealValidationAdapter(
    private val candidateDealQueryPort: CandidateDealQueryPort,
) : CandidateDealValidator {

    override fun validatePendingStatus(candidateDealId: Long) {
        val candidate = candidateDealQueryPort.loadById(candidateDealId)
        if (candidate.status != CandidateDealStatus.PENDING) {
            throw BusinessException(ApiResponseCode.INVALID_REQUEST)
        }
    }
}
