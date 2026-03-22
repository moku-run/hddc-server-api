package dev.hddc.domains.admin.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface AdminRepository : JpaRepository<AdminEntity, Long> {
    fun findByEmailAndIsActiveTrue(email: String): AdminEntity?
}
