package dev.hddc.domains.user.application.ports.output.validation

interface PasswordValidator {
    fun validatePasswordPattern(password: String)
    fun validatePasswordMatch(password: String, confirm: String)
}
