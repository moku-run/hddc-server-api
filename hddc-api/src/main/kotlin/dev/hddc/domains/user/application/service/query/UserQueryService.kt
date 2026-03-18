package dev.hddc.domains.user.application.service.query

import dev.hddc.domains.user.application.ports.input.query.CheckNicknameResult
import dev.hddc.domains.user.application.ports.input.query.CheckNicknameUsecase
import dev.hddc.domains.user.application.ports.output.query.UserQueryPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserQueryService(
    private val userQueryPort: UserQueryPort,
) : CheckNicknameUsecase {

    @Transactional(readOnly = true)
    override fun execute(nickname: String): CheckNicknameResult {
        val exists = userQueryPort.existsByNickname(nickname)
        return CheckNicknameResult(available = !exists, nickname = nickname)
    }
}
