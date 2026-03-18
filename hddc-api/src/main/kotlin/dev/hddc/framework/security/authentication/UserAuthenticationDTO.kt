package dev.hddc.framework.security.authentication

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class UserAuthenticationDTO(
    val userId: Long,
    val email: String,
    val nickname: String,
    private val encodedPassword: String,
    val userRole: String,
    val isNotDeleted: Boolean,
    val isNonLocked: Boolean,
) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(SimpleGrantedAuthority("ROLE_$userRole"))

    override fun getPassword(): String = encodedPassword
    override fun getUsername(): String = email
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = isNonLocked
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = isNotDeleted
}
