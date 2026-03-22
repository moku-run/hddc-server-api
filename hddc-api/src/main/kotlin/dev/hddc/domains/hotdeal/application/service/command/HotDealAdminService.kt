package dev.hddc.domains.hotdeal.application.service.command

import dev.hddc.domains.hotdeal.application.ports.input.command.CreateHotDealCommand
import dev.hddc.domains.hotdeal.application.ports.input.command.HotDealAdminUsecase
import dev.hddc.domains.hotdeal.application.ports.input.command.UpdateHotDealCommand
import dev.hddc.domains.hotdeal.application.ports.output.command.HotDealCommandPort
import dev.hddc.domains.hotdeal.application.ports.output.query.HotDealQueryPort
import dev.hddc.domains.hotdeal.domain.model.HotDealModel
import dev.hddc.framework.api.response.ApiResponseCode
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HotDealAdminService(
    private val hotDealCommandPort: HotDealCommandPort,
    private val hotDealQueryPort: HotDealQueryPort,
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
        return hotDealCommandPort.save(model)
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
    }
}
