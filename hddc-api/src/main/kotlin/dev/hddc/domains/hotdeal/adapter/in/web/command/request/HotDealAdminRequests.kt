package dev.hddc.domains.hotdeal.adapter.`in`.web.command.request

import dev.hddc.domains.hotdeal.application.ports.input.command.CreateHotDealCommand
import dev.hddc.domains.hotdeal.application.ports.input.command.UpdateHotDealCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateHotDealRequest(
    @field:NotBlank(message = "제목은 필수입니다.")
    @field:Size(max = 200, message = "제목은 200자 이하여야 합니다.")
    val title: String,
    val description: String? = null,
    @field:NotBlank(message = "URL은 필수입니다.")
    val url: String,
    val imageUrl: String? = null,
    val originalPrice: Int? = null,
    val dealPrice: Int? = null,
    val discountRate: Int? = null,
    val category: String? = null,
    val store: String? = null,
) {
    fun toCommand() = CreateHotDealCommand(
        title = title,
        description = description,
        url = url,
        imageUrl = imageUrl,
        originalPrice = originalPrice,
        dealPrice = dealPrice,
        discountRate = discountRate,
        category = category,
        store = store,
    )
}

data class UpdateHotDealRequest(
    val title: String? = null,
    val description: String? = null,
    val url: String? = null,
    val imageUrl: String? = null,
    val originalPrice: Int? = null,
    val dealPrice: Int? = null,
    val discountRate: Int? = null,
    val category: String? = null,
    val store: String? = null,
) {
    fun toCommand() = UpdateHotDealCommand(
        title = title,
        description = description,
        url = url,
        imageUrl = imageUrl,
        originalPrice = originalPrice,
        dealPrice = dealPrice,
        discountRate = discountRate,
        category = category,
        store = store,
    )
}
