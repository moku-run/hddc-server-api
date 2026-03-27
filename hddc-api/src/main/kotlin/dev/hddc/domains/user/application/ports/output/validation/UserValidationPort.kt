package dev.hddc.domains.user.application.ports.output.validation

interface UserValidationPort {
    fun requireEmailNotExists(email: String)
    fun requireNicknameNotExists(nickname: String)
    fun requireUserExistsByEmail(email: String)
}
