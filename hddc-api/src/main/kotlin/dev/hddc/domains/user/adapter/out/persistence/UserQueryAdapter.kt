package dev.hddc.domains.user.adapter.out.persistence

import dev.hddc.domains.user.application.ports.output.query.UserQueryPort
import dev.hddc.domains.user.domain.model.UserModel
import dev.hddc.framework.api.response.ApiResponseCode
import org.springframework.stereotype.Repository

@Repository
class UserQueryAdapter(
    private val userRepository: UserRepository,
) : UserQueryPort {

    override fun findByEmail(email: String): UserModel? =
        userRepository.findByEmailAndIsDeletedFalse(email)?.toDomain()

    override fun loadByEmail(email: String): UserModel =
        userRepository.findByEmailAndIsDeletedFalse(email)?.toDomain()
            ?: throw IllegalArgumentException(ApiResponseCode.USER_NOT_FOUND.code)

    override fun existsByEmail(email: String): Boolean =
        userRepository.existsByEmailAndIsDeletedFalse(email)

    override fun notExistsByEmail(email: String): Boolean =
        !userRepository.existsByEmailAndIsDeletedFalse(email)

    override fun existsByNickname(nickname: String): Boolean =
        userRepository.existsByNicknameAndIsDeletedFalse(nickname)

    override fun notExistsByNickname(nickname: String): Boolean =
        !userRepository.existsByNicknameAndIsDeletedFalse(nickname)

    override fun findNicknamesByIds(userIds: List<Long>): Map<Long, String> =
        userRepository.findAllByIdIn(userIds).associate { it.id!! to it.nickname }
}
