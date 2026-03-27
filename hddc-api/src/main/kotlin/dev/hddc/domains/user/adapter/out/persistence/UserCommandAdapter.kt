package dev.hddc.domains.user.adapter.out.persistence

import dev.hddc.domains.user.application.ports.output.command.UserCommandPort
import dev.hddc.domains.user.domain.model.CreateUserModel
import org.springframework.stereotype.Component

@Component
class UserCommandAdapter(
    private val userRepository: UserRepository,
) : UserCommandPort {

    override fun create(model: CreateUserModel): Long {
        val entity = model.toNewEntity()
        return userRepository.save(entity).id!!
    }

    override fun updateLoginSuccess(userId: Long) {
        val entity = userRepository.loadById(userId)
        entity.loginAttemptCount = 0
        userRepository.save(entity)
    }

    override fun updateLoginFailed(userId: Long, attemptCount: Int, locked: Boolean) {
        val entity = userRepository.loadById(userId)
        entity.loginAttemptCount = attemptCount
        entity.isLocked = locked
        userRepository.save(entity)
    }

    override fun updatePassword(userId: Long, encodedPassword: String) {
        val entity = userRepository.loadById(userId)
        entity.password = encodedPassword
        entity.loginAttemptCount = 0
        entity.isLocked = false
        userRepository.save(entity)
    }
}
