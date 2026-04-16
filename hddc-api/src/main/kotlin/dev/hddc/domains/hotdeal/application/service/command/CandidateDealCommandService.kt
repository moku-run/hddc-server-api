package dev.hddc.domains.hotdeal.application.service.command

import dev.hddc.domains.hotdeal.application.ports.input.command.BulkApproveResult
import dev.hddc.domains.hotdeal.application.ports.input.command.CandidateDealAdminUsecase
import dev.hddc.domains.hotdeal.application.ports.output.command.CandidateDealCommandPort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommandPort
import dev.hddc.domains.hotdeal.application.ports.output.event.DomainEventPublisher
import dev.hddc.domains.hotdeal.application.ports.output.query.CandidateDealQueryPort
import dev.hddc.domains.hotdeal.application.ports.output.validator.CandidateDealValidator
import dev.hddc.domains.hotdeal.domain.event.DealEvent
import dev.hddc.domains.hotdeal.domain.model.CandidateDealStatus
import dev.hddc.domains.hotdeal.domain.model.CreateHotDealModel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CandidateDealCommandService(
    private val candidateDealQueryPort: CandidateDealQueryPort,
    private val candidateDealCommandPort: CandidateDealCommandPort,
    private val candidateDealValidator: CandidateDealValidator,
    private val hotDealCommandPort: HotDealCommandPort,
    private val eventPublisher: DomainEventPublisher,
) : CandidateDealAdminUsecase {

    @Transactional
    override fun approve(id: Long): Long {
        candidateDealValidator.validatePending(id)
        val candidate = candidateDealQueryPort.loadById(id)
        val hotDeal = hotDealCommandPort.create(
            CreateHotDealModel(
                userId = candidate.userId,
                title = candidate.title ?: "",
                url = candidate.url ?: "",
                imageUrl = candidate.imageUrl,
                originalPrice = candidate.originalPrice,
                dealPrice = candidate.dealPrice,
                category = candidate.category,
                store = candidate.store,
            )
        )
        eventPublisher.publish(DealEvent.DealCreated(dealId = hotDeal.id))
        candidateDealCommandPort.updateStatus(id, CandidateDealStatus.TRANSFERRED)
        return hotDeal.id
    }

    @Transactional
    override fun reject(id: Long) {
        candidateDealValidator.validatePending(id)
        candidateDealCommandPort.updateStatus(id, CandidateDealStatus.REJECTED)
    }

    @Transactional
    override fun bulkApprove(ids: List<Long>): BulkApproveResult {
        val succeeded = ids.map { id ->
            candidateDealValidator.validatePending(id)
            val candidate = candidateDealQueryPort.loadById(id)
            val hotDeal = hotDealCommandPort.create(
                CreateHotDealModel(
                    userId = candidate.userId,
                    title = candidate.title ?: "",
                    url = candidate.url ?: "",
                    imageUrl = candidate.imageUrl,
                    originalPrice = candidate.originalPrice,
                    dealPrice = candidate.dealPrice,
                    category = candidate.category,
                    store = candidate.store,
                )
            )
            eventPublisher.publish(DealEvent.DealCreated(dealId = hotDeal.id))
            candidateDealCommandPort.updateStatus(id, CandidateDealStatus.TRANSFERRED)
            hotDeal.id
        }
        return BulkApproveResult(succeeded = succeeded, failed = emptyList())
    }
}
