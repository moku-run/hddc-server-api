package dev.hddc.domains.user.adapter.out.persistence

import dev.hddc.domains.user.domain.model.UserModel

fun UserEntity.toDomain(): UserModel = UserModel(
    id = id,
    email = email,
    password = password,
    nickname = nickname,
    role = role,
    isDeleted = isDeleted,
    isLocked = isLocked,
    loginAttemptCount = loginAttemptCount,
    lastLoginAt = lastLoginAt,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun UserModel.toEntity(): UserEntity = UserEntity(
    email = email,
    password = password,
    nickname = nickname,
    role = role,
    isDeleted = isDeleted,
    isLocked = isLocked,
    loginAttemptCount = loginAttemptCount,
    lastLoginAt = lastLoginAt,
    updatedAt = updatedAt,
).also {
    it.id = id
    it.createdAt = createdAt
}
