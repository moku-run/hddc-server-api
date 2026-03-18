package dev.hddc.framework.security.jwt.spec

object JwtSpec {
    const val TOKEN_HEADER = "Authorization"
    const val TOKEN_PREFIX = "Bearer "
    const val CLAIM_USERNAME = "username"
    const val CLAIM_ROLE = "role"
    const val BLACK_LIST_KEY = "jwt:blacklist:"
}
