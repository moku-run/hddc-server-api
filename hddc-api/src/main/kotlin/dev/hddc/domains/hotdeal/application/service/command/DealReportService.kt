package dev.hddc.domains.hotdeal.application.service.command

import dev.hddc.domains.hotdeal.application.ports.input.command.DealReportUsecase
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommandPort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommentPort
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealReportPort
import dev.hddc.domains.hotdeal.domain.model.HotDealCommentReportModel
import dev.hddc.domains.hotdeal.domain.model.HotDealReportModel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DealReportService(
    private val hotDealCommandPort: HotDealCommandPort,
    private val hotDealCommentPort: HotDealCommentPort,
    private val hotDealReportPort: HotDealReportPort,
) : DealReportUsecase {

    @Transactional
    override fun reportDeal(userId: Long, dealId: Long, reason: String) {
        hotDealCommandPort.loadById(dealId)
        hotDealReportPort.saveDealReport(HotDealReportModel(dealId = dealId, userId = userId, reason = reason))
    }

    @Transactional
    override fun reportComment(userId: Long, dealId: Long, commentId: Long, reason: String) {
        hotDealCommandPort.loadById(dealId)
        hotDealCommentPort.loadById(commentId)
        hotDealReportPort.saveCommentReport(HotDealCommentReportModel(commentId = commentId, userId = userId, reason = reason))
    }
}
