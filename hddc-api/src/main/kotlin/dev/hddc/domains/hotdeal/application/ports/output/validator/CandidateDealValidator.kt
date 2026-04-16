package dev.hddc.domains.hotdeal.application.ports.output.validator

interface CandidateDealValidator {
    fun validatePending(id: Long)
}
