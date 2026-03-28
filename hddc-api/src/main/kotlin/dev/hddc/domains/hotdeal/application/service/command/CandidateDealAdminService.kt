package dev.hddc.domains.hotdeal.application.service.command

import dev.hddc.domains.hotdeal.application.ports.input.command.ApproveResult
import dev.hddc.domains.hotdeal.application.ports.input.command.CandidateDealAdminUsecase
import dev.hddc.domains.hotdeal.application.ports.output.command.CandidateDealPort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommandPort
import dev.hddc.domains.hotdeal.application.ports.output.command.CandidateDealPageData
import dev.hddc.domains.hotdeal.domain.model.CandidateDealModel
import dev.hddc.domains.hotdeal.domain.model.CreateHotDealModel
import dev.hddc.domains.hotdeal.domain.spec.HotDealSpec
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class CandidateDealAdminService(
    private val candidateDealPort: CandidateDealPort,
    private val hotDealCommandPort: HotDealCommandPort,
) : CandidateDealAdminUsecase {

    @Transactional(readOnly = true)
    override fun getCandidateDeals(status: String, page: Int, size: Int): CandidateDealPageData =
        candidateDealPort.findByStatus(status, page, size)

    @Transactional
    override fun approve(candidateDealId: Long): Long {
        val candidate = candidateDealPort.findById(candidateDealId)
            ?: throw IllegalArgumentException("CANDIDATE_DEAL_NOT_FOUND")

        require(candidate.status == "PENDING") {
            "INVALID_REQUEST"
        }

        val saved = hotDealCommandPort.create(transferToCreateModel(candidate))

        candidateDealPort.updateStatus(candidateDealId, "APPROVED", Instant.now())

        return saved.id
    }

    @Transactional
    override fun reject(candidateDealId: Long) {
        val candidate = candidateDealPort.findById(candidateDealId)
            ?: throw IllegalArgumentException("CANDIDATE_DEAL_NOT_FOUND")

        require(candidate.status == "PENDING") {
            "INVALID_REQUEST"
        }

        candidateDealPort.updateStatus(candidateDealId, "REJECTED")
    }

    @Transactional
    override fun bulkApprove(candidateDealIds: List<Long>): ApproveResult {
        val candidates = candidateDealPort.findAllByIdsAndStatus(candidateDealIds, "PENDING")

        candidates.forEach { candidate ->
            hotDealCommandPort.create(transferToCreateModel(candidate))
            candidateDealPort.updateStatus(candidate.id!!, "APPROVED", Instant.now())
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
