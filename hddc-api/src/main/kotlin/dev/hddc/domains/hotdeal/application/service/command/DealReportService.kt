package dev.hddc.domains.hotdeal.application.service.command

import dev.hddc.domains.hotdeal.application.ports.input.command.DealReportUsecase
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealReportPort
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealCommentQueryPort
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealQueryPort
import dev.hddc.domains.hotdeal.domain.model.HotDealCommentReportModel
import dev.hddc.domains.hotdeal.domain.model.HotDealReportModel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DealReportService(
    private val hotDealQueryPort: HotDealQueryPort,
    private val hotDealCommentQueryPort: HotDealCommentQueryPort,
    private val hotDealReportPort: HotDealReportPort,
) : DealReportUsecase {

    @Transactional
    override fun reportDeal(userId: Long, dealId: Long, reason: String) {
        hotDealQueryPort.loadById(dealId)
        hotDealReportPort.saveDealReport(HotDealReportModel(dealId = dealId, userId = userId, reason = reason))
    }

    @Transactional
    override fun reportComment(userId: Long, dealId: Long, commentId: Long, reason: String) {
        hotDealQueryPort.loadById(dealId)
        hotDealCommentQueryPort.loadById(commentId)
        hotDealReportPort.saveCommentReport(HotDealCommentReportModel(commentId = commentId, userId = userId, reason = reason))
    }
}
