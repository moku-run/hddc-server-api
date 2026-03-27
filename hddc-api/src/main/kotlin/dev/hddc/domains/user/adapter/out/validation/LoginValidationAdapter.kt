package dev.hddc.domains.user.adapter.out.validation

import dev.hddc.domains.user.application.ports.output.security.PasswordEncodePort
import dev.hddc.domains.user.application.ports.output.validation.LoginValidator
import dev.hddc.domains.user.domain.model.UserModel
import dev.hddc.framework.api.response.ApiResponseCode
import org.springframework.stereotype.Component

@Component
class LoginValidationAdapter(
    private val passwordEncodePort: PasswordEncodePort,
) : LoginValidator {

    override fun requireActiveUser(user: UserModel) {
        require(!user.isDeleted) { ApiResponseCode.USER_DELETED.code }
        require(!user.isLocked) { ApiResponseCode.ACCOUNT_LOCKED.code }
    }

    override fun requirePasswordMatch(rawPassword: String, encodedPassword: String) {
        require(passwordEncodePort.matches(rawPassword, encodedPassword)) {
            ApiResponseCode.INVALID_CREDENTIALS.code
        }
    }
}
