package dev.hddc.domains.user.domain.spec

object LoginSpec {
    const val MAX_LOGIN_ATTEMPTS = 5

    fun isLockRequired(attemptCount: Int): Boolean = attemptCount >= MAX_LOGIN_ATTEMPTS
}
