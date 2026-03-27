package dev.hddc.domains.user.adapter.out.validation

import dev.hddc.domains.user.application.ports.output.validation.PasswordValidator
import dev.hddc.domains.user.domain.spec.PasswordSpec
import dev.hddc.framework.api.response.ApiResponseCode
import org.springframework.stereotype.Component

@Component
class PasswordValidationAdapter : PasswordValidator {

    override fun validatePasswordPattern(password: String) {
        require(PasswordSpec.isValidPattern(password)) {
            ApiResponseCode.USER_PASSWORD_MISMATCH.code
        }
    }

    override fun validatePasswordMatch(password: String, confirm: String) {
        require(PasswordSpec.isMatched(password, confirm)) {
            ApiResponseCode.USER_PASSWORD_MISMATCH.code
        }
    }
}
