package dev.hddc.domains.profile.domain.model

enum class LinkStyle(val value: String) {
    NONE("none"),
    FILL("fill"),
    OUTLINE("outline"),
    SHADOW("shadow"),
    ROUNDED("rounded"),
    PILL("pill"),
    GLASS("glass");

    companion object {
        fun fromValue(value: String): LinkStyle =
            entries.find { it.value == value }
                ?: throw IllegalArgumentException("Invalid LinkStyle: $value")
    }
}
