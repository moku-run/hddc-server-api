package dev.hddc.domains.profile.domain.model

enum class LinkLayout(val value: String) {
    LIST("list"),
    GRID_2("grid-2"),
    GRID_3("grid-3");

    companion object {
        fun fromValue(value: String): LinkLayout =
            entries.find { it.value == value }
                ?: throw IllegalArgumentException("Invalid LinkLayout: $value")
    }
}
