package dev.hddc.domains.hotdeal.domain.model

data class CreateHotDealModel(
    val userId: Long,
    val title: String,
    val description: String? = null,
    val url: String,
    val imageUrl: String? = null,
    val originalPrice: Int? = null,
    val dealPrice: Int? = null,
    val discountRate: Int? = null,
    val category: String? = null,
    val store: String? = null,
)
