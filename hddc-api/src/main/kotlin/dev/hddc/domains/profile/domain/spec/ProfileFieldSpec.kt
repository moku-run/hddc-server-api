package dev.hddc.domains.profile.domain.spec

import dev.hddc.domains.profile.domain.model.ColorTheme
import dev.hddc.domains.profile.domain.model.FontFamily
import dev.hddc.domains.profile.domain.model.HeaderLayout
import dev.hddc.domains.profile.domain.model.LinkAnimation
import dev.hddc.domains.profile.domain.model.LinkLayout
import dev.hddc.domains.profile.domain.model.LinkStyle
import dev.hddc.domains.profile.domain.model.PageLayout
import dev.hddc.domains.profile.domain.model.SocialPlatform

object ProfileFieldSpec {

    private val COLOR_THEMES = ColorTheme.entries.map { it.value }.toSet()
    private val FONT_FAMILIES = FontFamily.entries.map { it.value }.toSet()
    private val LINK_LAYOUTS = LinkLayout.entries.map { it.value }.toSet()
    private val LINK_STYLES = LinkStyle.entries.map { it.value }.toSet()
    private val HEADER_LAYOUTS = HeaderLayout.entries.map { it.value }.toSet()
    private val LINK_ANIMATIONS = LinkAnimation.entries.map { it.value }.toSet()
    private val SOCIAL_PLATFORMS = SocialPlatform.entries.map { it.value }.toSet()
    private val LINK_ROUNDS = setOf("none", "sm", "md", "lg")
    private val PAGE_LAYOUTS = PageLayout.entries.map { it.value }.toSet()
    private val LINK_BORDER_THICKS = setOf("none", "thin", "medium", "thick")
    private val BACKGROUND_TEXTURES = setOf("paper", "linen", "concrete", "fabric", "noise")

    fun validateColorTheme(value: String): Boolean = value in COLOR_THEMES
    fun validateFontFamily(value: String): Boolean = value in FONT_FAMILIES
    fun validateLinkLayout(value: String): Boolean = value in LINK_LAYOUTS
    fun validateLinkStyle(value: String): Boolean = value in LINK_STYLES
    fun validateHeaderLayout(value: String): Boolean = value in HEADER_LAYOUTS
    fun validateLinkAnimation(value: String): Boolean = value in LINK_ANIMATIONS
    fun validateSocialPlatform(value: String): Boolean = value in SOCIAL_PLATFORMS
    fun validateLinkRound(value: String): Boolean = value in LINK_ROUNDS
    fun validatePageLayout(value: String): Boolean = value in PAGE_LAYOUTS
    fun validateLinkBorderThick(value: String): Boolean = value in LINK_BORDER_THICKS
    fun validateBackgroundTexture(value: String?): Boolean = value == null || value in BACKGROUND_TEXTURES

    fun validateProfileFields(
        colorTheme: String,
        fontFamily: String,
        linkLayout: String,
        linkStyle: String,
        headerLayout: String,
        linkAnimation: String,
        linkRound: String,
        pageLayout: String,
        linkBorderThick: String,
        backgroundTexture: String?,
    ): String? {
        if (!validateColorTheme(colorTheme)) return "colorTheme: $colorTheme"
        if (!validateFontFamily(fontFamily)) return "fontFamily: $fontFamily"
        if (!validateLinkLayout(linkLayout)) return "linkLayout: $linkLayout"
        if (!validateLinkStyle(linkStyle)) return "linkStyle: $linkStyle"
        if (!validateHeaderLayout(headerLayout)) return "headerLayout: $headerLayout"
        if (!validateLinkAnimation(linkAnimation)) return "linkAnimation: $linkAnimation"
        if (!validateLinkRound(linkRound)) return "linkRound: $linkRound"
        if (!validatePageLayout(pageLayout)) return "pageLayout: $pageLayout"
        if (!validateLinkBorderThick(linkBorderThick)) return "linkBorderThick: $linkBorderThick"
        if (!validateBackgroundTexture(backgroundTexture)) return "backgroundTexture: $backgroundTexture"
        return null
    }
}
