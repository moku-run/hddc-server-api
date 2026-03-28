package dev.hddc.domains.hotdeal.adapter.out.command

import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.entity.HotDealEntity
import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.mapper.toDomain
import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.repository.HotDealRepository
import dev.hddc.domains.hotdeal.adapter.out.infrastructure.jpa.repository.loadById
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommandPort
import dev.hddc.domains.hotdeal.domain.model.CreateHotDealModel
import dev.hddc.domains.hotdeal.domain.model.HotDealModel
import org.springframework.stereotype.Component

@Component
class HotDealCommandAdapter(
    private val hotDealRepository: HotDealRepository,
) : HotDealCommandPort {

    override fun create(model: CreateHotDealModel): HotDealModel {
        val entity = HotDealEntity(
            userId = model.userId,
            title = model.title,
            description = model.description,
            url = model.url,
            imageUrl = model.imageUrl,
            originalPrice = model.originalPrice,
            dealPrice = model.dealPrice,
            discountRate = model.discountRate,
            category = model.category,
            store = model.store,
        )
        return hotDealRepository.save(entity).toDomain()
    }

    override fun updateLikeCount(dealId: Long, count: Int) {
        val entity = hotDealRepository.loadById(dealId)
        entity.likeCount = count
        hotDealRepository.save(entity)
    }

    override fun updateCommentCount(dealId: Long, count: Int) {
        val entity = hotDealRepository.loadById(dealId)
        entity.commentCount = count
        hotDealRepository.save(entity)
    }

    override fun updateClickCount(dealId: Long, count: Int) {
        val entity = hotDealRepository.loadById(dealId)
        entity.clickCount = count
        hotDealRepository.save(entity)
    }

    override fun updateExpiredVote(dealId: Long, count: Int, expired: Boolean) {
        val entity = hotDealRepository.loadById(dealId)
        entity.expiredVoteCount = count
        entity.isExpired = expired
        hotDealRepository.save(entity)
    }

    override fun softDelete(dealId: Long) {
        val entity = hotDealRepository.loadById(dealId)
        entity.softDelete()
        hotDealRepository.save(entity)
    }

    override fun update(dealId: Long, updater: (HotDealModel) -> HotDealModel): HotDealModel {
        val entity = hotDealRepository.loadById(dealId)
        val current = entity.toDomain()
        val updated = updater(current)
        entity.apply {
            title = updated.title
            description = updated.description
            url = updated.url
            imageUrl = updated.imageUrl
            originalPrice = updated.originalPrice
            dealPrice = updated.dealPrice
            discountRate = updated.discountRate
            category = updated.category
            store = updated.store
        }
        return hotDealRepository.save(entity).toDomain()
    }
}
