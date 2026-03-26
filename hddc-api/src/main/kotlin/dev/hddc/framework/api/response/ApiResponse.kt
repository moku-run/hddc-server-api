package dev.hddc.framework.api.response

import org.springframework.http.ResponseEntity

typealias ApiResult<T> = ResponseEntity<ApiResponse<T>>

data class ApiResponse<T>(
    val success: Boolean,
    val code: String,
    val message: String,
    val payload: T? = null,
) {
    companion object {
        fun <T> success(payload: T? = null): ResponseEntity<ApiResponse<T>> =
            of(ApiResponseCode.OK, payload)

        fun <T> successCreated(payload: T? = null): ResponseEntity<ApiResponse<T>> =
            of(ApiResponseCode.CREATED, payload)

        fun <T> successUpdated(payload: T? = null): ResponseEntity<ApiResponse<T>> =
            of(ApiResponseCode.UPDATED, payload)

        fun <T> successDeleted(): ResponseEntity<ApiResponse<Nothing>> =
            of(ApiResponseCode.DELETED, null)

        fun <T> of(code: ApiResponseCode, payload: T? = null): ResponseEntity<ApiResponse<T>> =
            ResponseEntity
                .status(code.status)
                .body(
                    ApiResponse(
                        success = code.success,
                        code = code.code,
                        message = code.message,
                        payload = payload,
                    )
                )

        fun error(code: ApiResponseCode): ResponseEntity<ApiResponse<Nothing>> =
            of(code, null)
    }
}
