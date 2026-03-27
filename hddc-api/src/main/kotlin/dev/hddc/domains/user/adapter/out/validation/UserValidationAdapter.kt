package dev.hddc.domains.user.adapter.out.validation

import dev.hddc.domains.user.application.ports.output.query.UserQueryPort
import dev.hddc.domains.user.application.ports.output.validation.UserValidationPort
import dev.hddc.framework.api.response.ApiResponseCode
import org.springframework.stereotype.Component

@Component
class UserValidationAdapter(
    private val userQueryPort: UserQueryPort,
) : UserValidationPort {

    override fun requireEmailNotExists(email: String) {
        require(userQueryPort.notExistsByEmail(email)) {
            ApiResponseCode.USER_DUPLICATE_EMAIL.code
        }
    }

    override fun requireNicknameNotExists(nickname: String) {
        require(userQueryPort.notExistsByNickname(nickname)) {
            ApiResponseCode.USER_DUPLICATE_NICKNAME.code
        }
    }

    override fun requireUserExistsByEmail(email: String) {
        require(userQueryPort.existsByEmail(email)) {
            ApiResponseCode.USER_NOT_FOUND.code
        }
    }
}
