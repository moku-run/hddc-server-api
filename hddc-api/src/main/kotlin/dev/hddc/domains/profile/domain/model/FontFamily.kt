package dev.hddc.domains.profile.domain.model

enum class FontFamily(val value: String) {
    PRETENDARD("pretendard"),
    NOTO_SANS("noto-sans"),
    NANUM_GOTHIC("nanum-gothic"),
    NANUM_MYEONGJO("nanum-myeongjo"),
    GOTHIC_A1("gothic-a1"),
    IBM_PLEX_SANS("ibm-plex-sans");

    companion object {
        fun fromValue(value: String): FontFamily =
            entries.find { it.value == value }
                ?: throw IllegalArgumentException("Invalid FontFamily: $value")
    }
}
