package dev.hddc.domains.user.domain.spec

object PasswordSpec {
    const val MIN_LENGTH = 8
    const val MAX_LENGTH = 20

    private const val PATTERN_STRING = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#\$%^&*()_=+.])(?!.*\\s).{8,20}\$"
    val PATTERN = Regex(PATTERN_STRING)

    fun validate(password: String): Boolean = PATTERN.matches(password)
}
