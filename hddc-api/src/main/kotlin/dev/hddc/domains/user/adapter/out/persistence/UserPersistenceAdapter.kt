package dev.hddc.domains.user.adapter.out.persistence

import dev.hddc.domains.user.application.ports.output.command.UserCommandPort
import dev.hddc.domains.user.application.ports.output.query.UserQueryPort
import dev.hddc.domains.user.domain.model.UserModel
import org.springframework.stereotype.Repository

@Repository
class UserPersistenceAdapter(
    private val userRepository: UserRepository,
) : UserQueryPort, UserCommandPort {

    override fun findByEmail(email: String): UserModel? =
        userRepository.findByEmailAndIsDeletedFalse(email)?.toDomain()

    override fun existsByEmail(email: String): Boolean =
        userRepository.existsByEmailAndIsDeletedFalse(email)

    override fun existsByNickname(nickname: String): Boolean =
        userRepository.existsByNicknameAndIsDeletedFalse(nickname)

    override fun findNicknamesByIds(userIds: List<Long>): Map<Long, String> =
        userRepository.findAllByIdIn(userIds).associate { it.id!! to it.nickname }

    override fun save(model: UserModel): UserModel {
        val entity = if (model.id != null) {
            val existing = userRepository.findById(model.id).orElse(null)
            if (existing != null) {
                existing.apply {
                    password = model.password
                    nickname = model.nickname
                    role = model.role
                    isDeleted = model.isDeleted
                    isLocked = model.isLocked
                    loginAttemptCount = model.loginAttemptCount
                }
                existing
            } else {
                model.toNewEntity()
            }
        } else {
            model.toNewEntity()
        }
        return userRepository.save(entity).toDomain()
    }
}
