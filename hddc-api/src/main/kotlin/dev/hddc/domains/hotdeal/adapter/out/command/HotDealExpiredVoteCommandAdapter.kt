package dev.hddc.domains.hotdeal.adapter.out.command

import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.entity.HotDealExpiredVoteEntity
import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.repository.HotDealExpiredVoteRepository
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealExpiredVotePort
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealExpiredVoteQueryPort
import dev.hddc.domains.hotdeal.domain.model.HotDealExpiredVoteModel
import org.springframework.stereotype.Component

@Component
class HotDealExpiredVoteCommandAdapter(
    private val hotDealExpiredVoteRepository: HotDealExpiredVoteRepository,
) : HotDealExpiredVotePort, HotDealExpiredVoteQueryPort {

    override fun existsByDealIdAndUserId(dealId: Long, userId: Long): Boolean =
        hotDealExpiredVoteRepository.existsByDealIdAndUserId(dealId, userId)

    override fun findByDealIdAndUserId(dealId: Long, userId: Long): HotDealExpiredVoteModel? =
        hotDealExpiredVoteRepository.findByDealIdAndUserId(dealId, userId)?.let {
            HotDealExpiredVoteModel(id = it.id, dealId = it.dealId, userId = it.userId, createdAt = it.createdAt)
        }

    override fun findAllByUserIdAndDealIds(userId: Long, dealIds: List<Long>): List<HotDealExpiredVoteModel> =
        hotDealExpiredVoteRepository.findAllByUserIdAndDealIdIn(userId, dealIds).map {
            HotDealExpiredVoteModel(id = it.id, dealId = it.dealId, userId = it.userId, createdAt = it.createdAt)
        }

    override fun save(model: HotDealExpiredVoteModel): HotDealExpiredVoteModel {
        val entity = HotDealExpiredVoteEntity(dealId = model.dealId, userId = model.userId)
        val saved = hotDealExpiredVoteRepository.save(entity)
        return HotDealExpiredVoteModel(id = saved.id, dealId = saved.dealId, userId = saved.userId, createdAt = saved.createdAt)
    }

    override fun deleteByDealIdAndUserId(dealId: Long, userId: Long): Boolean {
        val entity = hotDealExpiredVoteRepository.findByDealIdAndUserId(dealId, userId) ?: return false
        hotDealExpiredVoteRepository.delete(entity)
        return true
    }
}
