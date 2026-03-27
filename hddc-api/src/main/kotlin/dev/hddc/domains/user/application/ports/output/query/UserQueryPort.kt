package dev.hddc.domains.user.application.ports.output.query

import dev.hddc.domains.user.domain.model.UserModel

interface UserQueryPort {
    fun findByEmail(email: String): UserModel?
    fun existsByEmail(email: String): Boolean
    fun notExistsByEmail(email: String): Boolean
    fun existsByNickname(nickname: String): Boolean
    fun notExistsByNickname(nickname: String): Boolean
    fun findNicknamesByIds(userIds: List<Long>): Map<Long, String>
}
