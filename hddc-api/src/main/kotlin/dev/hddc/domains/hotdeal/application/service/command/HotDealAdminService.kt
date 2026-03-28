package dev.hddc.domains.hotdeal.application.service.command

import dev.hddc.domains.hotdeal.application.ports.input.command.CreateHotDealCommand
import dev.hddc.domains.hotdeal.application.ports.input.command.HotDealAdminUsecase
import dev.hddc.domains.hotdeal.application.ports.input.command.UpdateHotDealCommand
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommandPort
import dev.hddc.domains.hotdeal.application.ports.input.query.AdminHotDealPageResult
import dev.hddc.domains.hotdeal.application.ports.input.query.HotDealWithNickname
import dev.hddc.domains.hotdeal.application.ports.output.event.DomainEventPublisher
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealQueryPort
import dev.hddc.domains.hotdeal.domain.event.DealSseEvent
import dev.hddc.domains.hotdeal.domain.model.CreateHotDealModel
import dev.hddc.domains.hotdeal.domain.model.HotDealModel
import dev.hddc.domains.user.application.ports.output.query.UserQueryPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HotDealAdminService(
    private val hotDealQueryPort: HotDealQueryPort,
    private val hotDealCommandPort: HotDealCommandPort,
    private val userQueryPort: UserQueryPort,
    private val eventPublisher: DomainEventPublisher,
) : HotDealAdminUsecase {

    @Transactional(readOnly = true)
    override fun getAll(page: Int, size: Int): AdminHotDealPageResult {
        val data = hotDealQueryPort.findAll(page, size)
        val userIds = data.content.map { it.userId }.distinct()
        val nicknames = userQueryPort.findNicknamesByIds(userIds)
        return AdminHotDealPageResult(
            content = data.content.mapIndexed { index, deal ->
                HotDealWithNickname(
                    deal = deal,
                    nickname = nicknames[deal.userId] ?: "알 수 없음",
                    dealNumber = data.totalElements - (data.page.toLong() * data.size) - index,
                )
            },
            page = data.page,
            size = data.size,
            totalElements = data.totalElements,
            totalPages = data.totalPages,
        )
    }

    @Transactional
    override fun create(adminUserId: Long, command: CreateHotDealCommand): HotDealModel {
        val saved = hotDealCommandPort.create(
            CreateHotDealModel(
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
        )
        val nicknames = userQueryPort.findNicknamesByIds(listOf(saved.userId))
        eventPublisher.publish(DealSseEvent.NewDeal(
            id = saved.id,
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
    override fun update(dealId: Long, command: UpdateHotDealCommand): HotDealWithNickname {
        val updated = hotDealCommandPort.update(dealId) { existing ->
            existing.copy(
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
        }
        val nicknames = userQueryPort.findNicknamesByIds(listOf(updated.userId))
        return HotDealWithNickname(
            deal = updated,
            nickname = nicknames[updated.userId] ?: "알 수 없음",
        )
    }

    @Transactional
    override fun delete(dealId: Long) {
        hotDealQueryPort.loadById(dealId)
        hotDealCommandPort.softDelete(dealId)
        eventPublisher.publish(DealSseEvent.DealDeleted(id = dealId))
    }
}
