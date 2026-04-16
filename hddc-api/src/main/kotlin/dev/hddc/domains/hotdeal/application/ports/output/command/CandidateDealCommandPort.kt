package dev.hddc.domains.hotdeal.application.ports.output.command

import dev.hddc.domains.hotdeal.domain.model.CandidateDealStatus

interface CandidateDealCommandPort {
    fun updateStatus(id: Long, status: CandidateDealStatus)
}
