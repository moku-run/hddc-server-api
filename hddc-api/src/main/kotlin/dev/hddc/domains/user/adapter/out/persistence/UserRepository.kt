package dev.hddc.domains.user.adapter.out.persistence

import dev.hddc.framework.api.response.ApiResponseCode
import dev.hddc.framework.api.response.BusinessException
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findByEmailAndIsDeletedFalse(email: String): UserEntity?
    fun existsByEmailAndIsDeletedFalse(email: String): Boolean
    fun existsByNicknameAndIsDeletedFalse(nickname: String): Boolean
    fun findAllByIdIn(ids: List<Long>): List<UserEntity>
}

// 확장함수: not-found → 에러 변환 재사용
fun UserRepository.loadById(id: Long): UserEntity =
    findById(id).orElseThrow {
        BusinessException(ApiResponseCode.USER_NOT_FOUND)
    }

fun UserRepository.loadByEmail(email: String): UserEntity =
    findByEmailAndIsDeletedFalse(email)
        ?: throw BusinessException(ApiResponseCode.USER_NOT_FOUND)
