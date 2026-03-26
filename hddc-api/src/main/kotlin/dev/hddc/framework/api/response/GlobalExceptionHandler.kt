package dev.hddc.framework.api.response

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.async.AsyncRequestNotUsableException

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(AsyncRequestNotUsableException::class)
    fun handleAsyncDisconnect(e: AsyncRequestNotUsableException) {
        // SSE 클라이언트 연결 끊김 — 정상 동작, 무시
    }

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
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        log.error("Unhandled exception", e)
        return ApiResponse.error(ApiResponseCode.INTERNAL_SERVER_ERROR)
    }
}
