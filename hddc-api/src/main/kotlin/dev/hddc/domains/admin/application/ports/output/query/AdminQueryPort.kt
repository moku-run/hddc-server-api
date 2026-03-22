package dev.hddc.domains.admin.application.ports.output.query

import dev.hddc.domains.admin.domain.model.AdminModel

interface AdminQueryPort {
    fun findByEmail(email: String): AdminModel?
}
