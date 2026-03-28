package dev.hddc.domains.hotdeal.application.service.command

import dev.hddc.domains.hotdeal.application.ports.input.command.ApproveResult
import dev.hddc.domains.hotdeal.application.ports.input.command.CandidateDealAdminUsecase
import dev.hddc.domains.hotdeal.application.ports.output.command.CandidateDealPort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommandPort
import dev.hddc.domains.hotdeal.application.ports.output.query.CandidateDealQueryPort
import dev.hddc.domains.hotdeal.application.ports.output.validator.CandidateDealValidator
import dev.hddc.domains.hotdeal.domain.model.CandidateDealModel
import dev.hddc.domains.hotdeal.domain.model.CandidateDealStatus
import dev.hddc.domains.hotdeal.domain.model.CreateHotDealModel
import dev.hddc.domains.hotdeal.domain.spec.HotDealSpec
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class CandidateDealAdminService(
    private val candidateDealQueryPort: CandidateDealQueryPort,
    private val candidateDealPort: CandidateDealPort,
    private val candidateDealValidator: CandidateDealValidator,
    private val hotDealCommandPort: HotDealCommandPort,
) : CandidateDealAdminUsecase {

    @Transactional
    override fun approve(candidateDealId: Long): Long {
        candidateDealValidator.validatePendingStatus(candidateDealId)
        val candidate = candidateDealQueryPort.loadById(candidateDealId)
        val saved = hotDealCommandPort.create(transferToCreateModel(candidate))
        candidateDealPort.updateStatus(candidateDealId, CandidateDealStatus.APPROVED.value, Instant.now())
        return saved.id
    }

    @Transactional
    override fun reject(candidateDealId: Long) {
        candidateDealValidator.validatePendingStatus(candidateDealId)
        candidateDealPort.updateStatus(candidateDealId, CandidateDealStatus.REJECTED.value)
    }

    @Transactional
    override fun bulkApprove(candidateDealIds: List<Long>): ApproveResult {
        val candidates = candidateDealQueryPort.findAllByIdsAndStatus(candidateDealIds, CandidateDealStatus.PENDING.value)
        candidates.forEach { candidate ->
            hotDealCommandPort.create(transferToCreateModel(candidate))
            candidateDealPort.updateStatus(candidate.id!!, CandidateDealStatus.APPROVED.value, Instant.now())
        }
        return ApproveResult(approvedCount = candidates.size)
    }

    private fun transferToCreateModel(candidate: CandidateDealModel) = CreateHotDealModel(
        userId = HotDealSpec.SYSTEM_USER_ID,
        title = candidate.title ?: "제목 없음",
        description = candidate.description,
        url = candidate.dealLink ?: candidate.postUrl,
        imageUrl = candidate.imageUrl,
        originalPrice = candidate.originalPrice,
        dealPrice = candidate.dealPrice,
        discountRate = candidate.discountRate,
        category = candidate.category,
        store = candidate.store,
    )
}
