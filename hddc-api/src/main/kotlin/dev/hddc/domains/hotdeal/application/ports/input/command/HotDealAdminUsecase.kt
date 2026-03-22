package dev.hddc.domains.hotdeal.application.ports.input.command

import dev.hddc.domains.hotdeal.domain.model.HotDealModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

data class CreateHotDealCommand(
    val title: String,
    val description: String?,
    val url: String,
    val imageUrl: String?,
    val originalPrice: Int?,
    val dealPrice: Int?,
    val discountRate: Int?,
    val category: String?,
    val store: String?,
)

data class UpdateHotDealCommand(
    val title: String?,
    val description: String?,
    val url: String?,
    val imageUrl: String?,
    val originalPrice: Int?,
    val dealPrice: Int?,
    val discountRate: Int?,
    val category: String?,
    val store: String?,
)

interface HotDealAdminUsecase {
    fun getAll(pageable: Pageable): Page<HotDealModel>
    fun create(adminUserId: Long, command: CreateHotDealCommand): HotDealModel
    fun update(dealId: Long, command: UpdateHotDealCommand): HotDealModel
    fun delete(dealId: Long)
}
