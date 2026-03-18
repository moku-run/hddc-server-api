package dev.hddc.domains.profile.domain.model

enum class LinkAnimation(val value: String) {
    NONE("none"),
    FADE_IN("fade-in"),
    SLIDE_UP("slide-up"),
    SCALE("scale"),
    STAGGER("stagger");

    companion object {
        fun fromValue(value: String): LinkAnimation =
            entries.find { it.value == value }
                ?: throw IllegalArgumentException("Invalid LinkAnimation: $value")
    }
}
