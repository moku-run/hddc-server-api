package dev.hddc.domains.profile.domain.model

enum class ColorTheme(val value: String) {
    TEAL("teal"),
    ORANGE("orange"),
    BLUE("blue"),
    PURPLE("purple"),
    PINK("pink"),
    RED("red"),
    YELLOW("yellow"),
    GREEN("green"),
    SLATE("slate"),
    ZINC("zinc"),
    CUSTOM("custom");

    companion object {
        fun fromValue(value: String): ColorTheme =
            entries.find { it.value == value }
                ?: throw IllegalArgumentException("Invalid ColorTheme: $value")
    }
}
