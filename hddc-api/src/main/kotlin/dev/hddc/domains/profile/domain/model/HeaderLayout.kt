package dev.hddc.domains.profile.domain.model

enum class HeaderLayout(val value: String) {
    CENTER("center"),
    LEFT("left"),
    AVATAR_ONLY("avatar-only"),
    BANNER_ONLY("banner-only");

    companion object {
        fun fromValue(value: String): HeaderLayout =
            entries.find { it.value == value }
                ?: throw IllegalArgumentException("Invalid HeaderLayout: $value")
    }
}
