package dev.hddc.framework.security.jwt.spec

object JwtSpec {
    const val TOKEN_HEADER = "Authorization"
    const val TOKEN_PREFIX = "Bearer "
    const val CLAIM_USERNAME = "username"
    const val CLAIM_ROLE = "role"
    const val CLAIM_TOKEN_TYPE = "type"
    const val TOKEN_TYPE_ACCESS = "access"
    const val TOKEN_TYPE_REFRESH = "refresh"
    const val BLACK_LIST_KEY = "jwt:blacklist:"
    const val REFRESH_KEY = "jwt:refresh:"
    const val REFRESH_COOKIE_NAME = "refresh_token"
}
