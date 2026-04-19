package dev.hddc.domains.hotdeal.application.service.command

import dev.hddc.domains.hotdeal.application.ports.input.command.BulkApproveResult
import dev.hddc.domains.hotdeal.application.ports.input.command.BulkRejectResult
import dev.hddc.domains.hotdeal.application.ports.input.command.CandidateDealAdminUsecase
import dev.hddc.domains.hotdeal.application.ports.output.checker.HotDealDuplicateChecker
import dev.hddc.domains.hotdeal.application.ports.output.command.CandidateDealCommandPort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommandPort
import dev.hddc.domains.hotdeal.application.ports.output.event.DomainEventPublisher
import dev.hddc.domains.hotdeal.application.ports.output.query.CandidateDealQueryPort
import dev.hddc.domains.hotdeal.application.ports.output.validator.CandidateDealValidator
import dev.hddc.domains.hotdeal.domain.event.DealEvent
import dev.hddc.domains.hotdeal.domain.model.CandidateDealStatus
import dev.hddc.domains.hotdeal.domain.model.CreateHotDealModel
import dev.hddc.framework.api.response.BusinessException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CandidateDealCommandService(
    private val candidateDealQueryPort: CandidateDealQueryPort,
    private val candidateDealCommandPort: CandidateDealCommandPort,
    private val candidateDealValidator: CandidateDealValidator,
    private val hotDealCommandPort: HotDealCommandPort,
    private val hotDealDuplicateChecker: HotDealDuplicateChecker,
    private val eventPublisher: DomainEventPublisher,
) : CandidateDealAdminUsecase {

    @Transactional
    override fun approve(id: Long): Long? {
        candidateDealValidator.validatePending(id)
        val candidate = candidateDealQueryPort.loadById(id)
        val url = candidate.url ?: ""
        if (url.isNotBlank() && hotDealDuplicateChecker.existsByUrl(url)) {
            candidateDealCommandPort.updateStatus(id, CandidateDealStatus.DUPLICATE_URL)
            return null
        }
        val hotDeal = hotDealCommandPort.create(
            CreateHotDealModel(
                userId = candidate.userId,
                title = candidate.title ?: "",
                url = url,
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
    override fun registerWithModifications(id: Long, command: CandidateDealAdminUsecase.RegisterCommand): Long? {
        candidateDealValidator.validatePending(id)
        if (command.url.isNotBlank() && hotDealDuplicateChecker.existsByUrl(command.url)) {
            candidateDealCommandPort.updateStatus(id, CandidateDealStatus.DUPLICATE_URL)
            return null
        }
        val candidate = candidateDealQueryPort.loadById(id)
        val hotDeal = hotDealCommandPort.create(
            CreateHotDealModel(
                userId = candidate.userId,
                title = command.title,
                url = command.url,
                imageUrl = command.imageUrl,
                originalPrice = command.originalPrice,
                dealPrice = command.dealPrice,
                category = command.category,
                store = command.store,
                description = command.description,
                discountRate = command.discountRate,
            )
        )
        eventPublisher.publish(DealEvent.DealCreated(dealId = hotDeal.id))
        candidateDealCommandPort.updateStatus(id, CandidateDealStatus.TRANSFERRED)
        return hotDeal.id
    }

    @Transactional
    override fun bulkReject(ids: List<Long>): BulkRejectResult {
        val succeeded = ids.map { id ->
            candidateDealValidator.validatePending(id)
            candidateDealCommandPort.updateStatus(id, CandidateDealStatus.REJECTED)
            id
        }
        return BulkRejectResult(succeeded = succeeded, failed = emptyList())
    }

    @Transactional
    override fun bulkApprove(ids: List<Long>): BulkApproveResult {
        val succeeded = mutableListOf<Long>()
        val failed = mutableListOf<Long>()
        val seenUrls = mutableSetOf<String>()

        ids.forEach { id ->
            try {
                candidateDealValidator.validatePending(id)
                val candidate = candidateDealQueryPort.loadById(id)
                val url = candidate.url ?: ""

                if (url.isNotBlank() && (hotDealDuplicateChecker.existsByUrl(url) || !seenUrls.add(url))) {
                    candidateDealCommandPort.updateStatus(id, CandidateDealStatus.DUPLICATE_URL)
                    failed.add(id)
                    return@forEach
                }

                val hotDeal = hotDealCommandPort.create(
                    CreateHotDealModel(
                        userId = candidate.userId,
                        title = candidate.title ?: "",
                        url = url,
                        imageUrl = candidate.imageUrl,
                        originalPrice = candidate.originalPrice,
                        dealPrice = candidate.dealPrice,
                        category = candidate.category,
                        store = candidate.store,
                    )
                )
                eventPublisher.publish(DealEvent.DealCreated(dealId = hotDeal.id))
                candidateDealCommandPort.updateStatus(id, CandidateDealStatus.TRANSFERRED)
                succeeded.add(hotDeal.id)
            } catch (e: BusinessException) {
                failed.add(id)
            }
        }
        return BulkApproveResult(succeeded = succeeded, failed = failed)
    }
}
