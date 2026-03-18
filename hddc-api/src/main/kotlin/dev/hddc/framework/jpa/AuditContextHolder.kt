package dev.hddc.framework.jpa

import dev.hddc.framework.security.authentication.UserAuthenticationDTO
import org.springframework.security.core.context.SecurityContextHolder

object AuditContextHolder {
    fun getCurrentUserId(): Long {
        val auth = SecurityContextHolder.getContext().authentication ?: return 0
        val principal = auth.principal
        return if (principal is UserAuthenticationDTO) principal.userId else 0
    }
}
