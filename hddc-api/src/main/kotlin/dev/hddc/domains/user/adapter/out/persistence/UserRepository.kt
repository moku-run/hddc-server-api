package dev.hddc.domains.user.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findByEmailAndIsDeletedFalse(email: String): UserEntity?
    fun existsByEmailAndIsDeletedFalse(email: String): Boolean
    fun existsByNicknameAndIsDeletedFalse(nickname: String): Boolean
}
