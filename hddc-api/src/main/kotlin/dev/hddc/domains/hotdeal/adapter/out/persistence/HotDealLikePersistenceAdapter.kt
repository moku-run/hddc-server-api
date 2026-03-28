package dev.hddc.domains.hotdeal.adapter.out.persistence

import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealLikePort
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealLikeQueryPort
import dev.hddc.domains.hotdeal.domain.model.HotDealLikeModel
import org.springframework.stereotype.Component

@Component
class HotDealLikePersistenceAdapter(
    private val hotDealLikeRepository: HotDealLikeRepository,
) : HotDealLikePort, HotDealLikeQueryPort {

    override fun existsByDealIdAndUserId(dealId: Long, userId: Long): Boolean =
        hotDealLikeRepository.existsByDealIdAndUserId(dealId, userId)

    override fun findByDealIdAndUserId(dealId: Long, userId: Long): HotDealLikeModel? =
        hotDealLikeRepository.findByDealIdAndUserId(dealId, userId)?.let {
            HotDealLikeModel(id = it.id, dealId = it.dealId, userId = it.userId, createdAt = it.createdAt)
        }

    override fun save(model: HotDealLikeModel): HotDealLikeModel {
        val entity = HotDealLikeEntity(dealId = model.dealId, userId = model.userId)
        val saved = hotDealLikeRepository.save(entity)
        return HotDealLikeModel(id = saved.id, dealId = saved.dealId, userId = saved.userId, createdAt = saved.createdAt)
    }

    override fun deleteByDealIdAndUserId(dealId: Long, userId: Long): Boolean {
        val entity = hotDealLikeRepository.findByDealIdAndUserId(dealId, userId) ?: return false
        hotDealLikeRepository.delete(entity)
        return true
    }

    override fun findAllByUserIdAndDealIds(userId: Long, dealIds: List<Long>): List<HotDealLikeModel> =
        hotDealLikeRepository.findAllByUserIdAndDealIdIn(userId, dealIds).map {
            HotDealLikeModel(id = it.id, dealId = it.dealId, userId = it.userId, createdAt = it.createdAt)
        }
}
