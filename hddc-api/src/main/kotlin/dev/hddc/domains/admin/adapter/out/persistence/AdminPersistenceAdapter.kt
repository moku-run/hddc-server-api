package dev.hddc.domains.admin.adapter.out.persistence

import dev.hddc.domains.admin.application.ports.output.query.AdminQueryPort
import dev.hddc.domains.admin.domain.model.AdminModel
import org.springframework.stereotype.Repository

@Repository
class AdminPersistenceAdapter(
    private val adminRepository: AdminRepository,
) : AdminQueryPort {

    override fun findByEmail(email: String): AdminModel? =
        adminRepository.findByEmailAndIsActiveTrue(email)?.toDomain()

    private fun AdminEntity.toDomain() = AdminModel(
        id = id,
        email = email,
        password = password,
        name = name,
        role = role,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}
