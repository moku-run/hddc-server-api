package dev.hddc.framework.security.authentication

import dev.hddc.domains.admin.adapter.out.persistence.AdminRepository
import dev.hddc.domains.user.adapter.out.persistence.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository,
    private val adminRepository: AdminRepository,
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val admin = adminRepository.findByEmailAndIsActiveTrue(username)
        if (admin != null) {
            return UserAuthenticationDTO(
                userId = admin.id!!,
                email = admin.email,
                nickname = admin.name,
                encodedPassword = admin.password,
                userRole = admin.role,
                isNotDeleted = true,
                isNonLocked = true,
            )
        }

        val user = userRepository.findByEmailAndIsDeletedFalse(username)
            ?: throw UsernameNotFoundException("User not found: $username")

        return UserAuthenticationDTO(
            userId = user.id!!,
            email = user.email,
            nickname = user.nickname,
            encodedPassword = user.password,
            userRole = user.role,
            isNotDeleted = !user.isDeleted,
            isNonLocked = !user.isLocked,
        )
    }
}
