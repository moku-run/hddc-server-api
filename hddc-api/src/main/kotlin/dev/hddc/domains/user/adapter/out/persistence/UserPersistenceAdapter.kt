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

    override fun save(model: UserModel): UserModel =
        userRepository.save(model.toEntity()).toDomain()
}
