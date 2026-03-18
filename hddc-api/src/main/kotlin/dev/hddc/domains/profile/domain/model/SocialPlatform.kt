package dev.hddc.domains.profile.domain.model

enum class SocialPlatform(val value: String) {
    INSTAGRAM("instagram"),
    YOUTUBE("youtube"),
    GITHUB("github"),
    TWITTER("twitter"),
    FACEBOOK("facebook"),
    LINKEDIN("linkedin"),
    TIKTOK("tiktok"),
    WEBSITE("website");

    companion object {
        fun fromValue(value: String): SocialPlatform =
            entries.find { it.value == value }
                ?: throw IllegalArgumentException("Invalid SocialPlatform: $value")
    }
}
