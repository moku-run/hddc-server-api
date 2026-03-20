package dev.hddc.domains.profile.adapter.out.persistence

import dev.hddc.framework.jpa.BaseAuditEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.Table

@Entity
@Table(name = "mst_profile")
class ProfileEntity(
    @Column(name = "user_id", nullable = false, unique = true)
    val userId: Long,

    @Column(nullable = false, unique = true, length = 30)
    var slug: String,

    @Column(nullable = false, length = 20)
    var nickname: String,

    @Column(length = 80)
    var bio: String? = null,

    @Column(name = "avatar_url", length = 1000)
    var avatarUrl: String? = null,

    @Column(name = "background_url", length = 1000)
    var backgroundUrl: String? = null,

    @Column(name = "background_color", length = 20)
    var backgroundColor: String? = null,

    @Column(name = "link_layout", nullable = false, length = 20)
    var linkLayout: String = "list",

    @Column(name = "link_style", nullable = false, length = 20)
    var linkStyle: String = "fill",

    @Column(name = "font_family", nullable = false, length = 30)
    var fontFamily: String = "pretendard",

    @Column(name = "header_layout", nullable = false, length = 20)
    var headerLayout: String = "center",

    @Column(name = "link_animation", nullable = false, length = 20)
    var linkAnimation: String = "none",

    @Column(name = "color_theme", nullable = false, length = 20)
    var colorTheme: String = "default",

    @Column(name = "custom_primary_color", length = 20)
    var customPrimaryColor: String? = null,

    @Column(name = "custom_secondary_color", length = 20)
    var customSecondaryColor: String? = null,

    @Column(name = "font_color", length = 20)
    var fontColor: String? = null,

    @Column(name = "link_round", nullable = false, length = 10)
    var linkRound: String = "sm",

    @Column(name = "dark_mode", nullable = false)
    var darkMode: Boolean = false,

    @OneToMany(mappedBy = "profile", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC")
    var links: MutableSet<ProfileLinkEntity> = LinkedHashSet(),

    @OneToMany(mappedBy = "profile", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC")
    var socials: MutableSet<ProfileSocialEntity> = LinkedHashSet(),
) : BaseAuditEntity()
