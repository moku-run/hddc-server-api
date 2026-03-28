package dev.hddc.framework.api.response

class BusinessException(val code: ApiResponseCode) : RuntimeException(code.message)
