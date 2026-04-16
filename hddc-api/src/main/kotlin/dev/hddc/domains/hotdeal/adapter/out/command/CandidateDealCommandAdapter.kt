package dev.hddc.domains.hotdeal.adapter.out.command

import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.repository.CandidateDealRepository
import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.repository.loadById
import dev.hddc.domains.hotdeal.application.ports.output.command.CandidateDealCommandPort
import dev.hddc.domains.hotdeal.domain.model.CandidateDealStatus
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class CandidateDealCommandAdapter(
    private val candidateDealRepository: CandidateDealRepository,
) : CandidateDealCommandPort {

    override fun updateStatus(id: Long, status: CandidateDealStatus) {
        val entity = candidateDealRepository.loadById(id)
        entity.status = status.name
        entity.updatedAt = Instant.now()
        candidateDealRepository.save(entity)
    }
}
