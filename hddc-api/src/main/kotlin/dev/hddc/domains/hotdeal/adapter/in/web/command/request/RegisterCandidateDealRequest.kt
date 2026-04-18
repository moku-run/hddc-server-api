package dev.hddc.domains.hotdeal.adapter.`in`.web.command.request

import dev.hddc.domains.hotdeal.application.ports.input.command.CandidateDealAdminUsecase

data class RegisterCandidateDealRequest(
    val title: String,
    val url: String,
    val imageUrl: String? = null,
    val originalPrice: Int? = null,
    val dealPrice: Int? = null,
    val store: String? = null,
    val category: String? = null,
    val description: String? = null,
    val discountRate: Int? = null,
) {
    fun toCommand() = CandidateDealAdminUsecase.RegisterCommand(
        title = title,
        url = url,
        imageUrl = imageUrl,
        originalPrice = originalPrice,
        dealPrice = dealPrice,
        store = store,
        category = category,
        description = description,
        discountRate = discountRate,
    )
}
