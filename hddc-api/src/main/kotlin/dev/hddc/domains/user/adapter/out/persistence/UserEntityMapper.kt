package dev.hddc.domains.user.adapter.out.persistence

import dev.hddc.domains.user.domain.model.CreateUserModel
import dev.hddc.domains.user.domain.model.UserModel

fun UserEntity.toDomain(): UserModel = UserModel(
    id = id!!,
    email = email,
    password = password,
    nickname = nickname,
    role = role,
    isDeleted = isDeleted,
    isLocked = isLocked,
    loginAttemptCount = loginAttemptCount,
)

fun CreateUserModel.toNewEntity(): UserEntity = UserEntity(
    email = email,
    password = password,
    nickname = nickname,
    role = role,
)
