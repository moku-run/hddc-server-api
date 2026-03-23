package dev.hddc.domains.profile.domain.model

enum class PageLayout(val value: String) {
    LIST("list"),
    CARD("card"),
    GRID("grid"),
    GRID_3("grid-3"),
    SHOP("shop"),
    VISUAL("visual");

    companion object {
        fun fromValue(value: String): PageLayout =
            entries.find { it.value == value }
                ?: throw IllegalArgumentException("Invalid PageLayout: $value")
    }
}
