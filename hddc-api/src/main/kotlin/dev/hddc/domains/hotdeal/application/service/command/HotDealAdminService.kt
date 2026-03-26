package dev.hddc.domains.hotdeal.application.service.command

import dev.hddc.domains.hotdeal.application.ports.input.command.CreateHotDealCommand
import dev.hddc.domains.hotdeal.application.ports.input.command.HotDealAdminUsecase
import dev.hddc.domains.hotdeal.application.ports.input.command.UpdateHotDealCommand
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommandPort
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealQueryPort
import dev.hddc.domains.hotdeal.domain.event.DealSseEvent
import dev.hddc.domains.hotdeal.domain.model.HotDealModel
import dev.hddc.domains.user.application.ports.output.query.UserQueryPort
import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.domains.hotdeal.application.ports.output.event.DomainEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HotDealAdminService(
    private val hotDealCommandPort: HotDealCommandPort,
    private val hotDealQueryPort: HotDealQueryPort,
    private val userQueryPort: UserQueryPort,
    private val eventPublisher: DomainEventPublisher,
) : HotDealAdminUsecase {

    @Transactional(readOnly = true)
    override fun getAll(pageable: Pageable): Page<HotDealModel> =
        hotDealQueryPort.findAll(pageable)

    @Transactional
    override fun create(adminUserId: Long, command: CreateHotDealCommand): HotDealModel {
        val model = HotDealModel(
            userId = adminUserId,
            title = command.title,
            description = command.description,
            url = command.url,
            imageUrl = command.imageUrl,
            originalPrice = command.originalPrice,
            dealPrice = command.dealPrice,
            discountRate = command.discountRate,
            category = command.category,
            store = command.store,
        )
        val saved = hotDealCommandPort.save(model)
        val nicknames = userQueryPort.findNicknamesByIds(listOf(saved.userId))
        eventPublisher.publish(DealSseEvent.NewDeal(
            id = saved.id!!,
            title = saved.title,
            dealPrice = saved.dealPrice,
            originalPrice = saved.originalPrice,
            discountRate = saved.discountRate,
            imageUrl = saved.imageUrl,
            nickname = nicknames[saved.userId] ?: "알 수 없음",
            store = saved.store,
            likeCount = saved.likeCount,
            commentCount = saved.commentCount,
            clickCount = saved.clickCount,
            createdAt = saved.createdAt,
        ))
        return saved
    }

    @Transactional
    override fun update(dealId: Long, command: UpdateHotDealCommand): HotDealModel {
        val existing = hotDealCommandPort.findById(dealId)
            ?: throw IllegalArgumentException(ApiResponseCode.HOT_DEAL_NOT_FOUND.code)

        val updated = existing.copy(
            title = command.title ?: existing.title,
            description = command.description ?: existing.description,
            url = command.url ?: existing.url,
            imageUrl = command.imageUrl ?: existing.imageUrl,
            originalPrice = command.originalPrice ?: existing.originalPrice,
            dealPrice = command.dealPrice ?: existing.dealPrice,
            discountRate = command.discountRate ?: existing.discountRate,
            category = command.category ?: existing.category,
            store = command.store ?: existing.store,
        )

        return hotDealCommandPort.save(updated)
    }

    @Transactional
    override fun delete(dealId: Long) {
        val existing = hotDealCommandPort.findById(dealId)
            ?: throw IllegalArgumentException(ApiResponseCode.HOT_DEAL_NOT_FOUND.code)

        hotDealCommandPort.save(existing.copy(isDeleted = true))
        eventPublisher.publish(DealSseEvent.DealDeleted(id = dealId))
    }
}
