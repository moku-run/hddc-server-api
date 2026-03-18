package dev.hddc.framework.api.response

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(e: IllegalArgumentException): ResponseEntity<ApiResponse<Nothing>> {
        val code = ApiResponseCode.findByCode(e.message ?: "")
            ?: ApiResponseCode.INVALID_REQUEST
        return ApiResponse.error(code)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(e: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Nothing>> {
        val message = e.bindingResult.fieldErrors
            .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }

        return ResponseEntity
            .status(ApiResponseCode.INVALID_REQUEST.status)
            .body(
                ApiResponse(
                    success = false,
                    code = ApiResponseCode.INVALID_REQUEST.code,
                    message = message,
                )
            )
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalState(e: IllegalStateException): ResponseEntity<ApiResponse<Nothing>> {
        val code = ApiResponseCode.findByCode(e.message ?: "")
            ?: ApiResponseCode.INTERNAL_SERVER_ERROR
        return ApiResponse.error(code)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Nothing>> =
        ApiResponse.error(ApiResponseCode.INTERNAL_SERVER_ERROR)
}
