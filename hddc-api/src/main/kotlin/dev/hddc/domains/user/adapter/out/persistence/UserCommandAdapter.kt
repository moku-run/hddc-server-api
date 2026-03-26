package dev.hddc.domains.user.adapter.out.persistence

import dev.hddc.domains.user.application.ports.output.command.UserCommandPort
import dev.hddc.domains.user.domain.model.UserModel
import dev.hddc.framework.api.response.ApiResponseCode
import org.springframework.stereotype.Repository

@Repository
class UserCommandAdapter(
    private val userRepository: UserRepository,
) : UserCommandPort {

    override fun create(model: UserModel): UserModel {
        val entity = model.toNewEntity()
        return userRepository.save(entity).toDomain()
    }

    override fun updateLoginSuccess(userId: Long) {
        val entity = findEntity(userId)
        entity.loginAttemptCount = 0
        userRepository.save(entity)
    }

    override fun updateLoginFailed(userId: Long, attemptCount: Int, locked: Boolean) {
        val entity = findEntity(userId)
        entity.loginAttemptCount = attemptCount
        entity.isLocked = locked
        userRepository.save(entity)
    }

    override fun updatePassword(userId: Long, encodedPassword: String) {
        val entity = findEntity(userId)
        entity.password = encodedPassword
        entity.loginAttemptCount = 0
        entity.isLocked = false
        userRepository.save(entity)
    }

    private fun findEntity(userId: Long): UserEntity =
        userRepository.findById(userId).orElseThrow {
            IllegalArgumentException(ApiResponseCode.USER_NOT_FOUND.code)
        }
}
