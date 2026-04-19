package dev.hddc.framework.api.response

import org.springframework.http.HttpStatus

enum class ApiResponseCode(
    val status: HttpStatus,
    val success: Boolean,
    val code: String,
    val message: String,
) {
    // Success
    OK(HttpStatus.OK, true, "S001", "요청이 성공적으로 처리되었습니다."),
    CREATED(HttpStatus.CREATED, true, "S002", "리소스가 성공적으로 생성되었습니다."),
    UPDATED(HttpStatus.OK, true, "S003", "리소스가 성공적으로 수정되었습니다."),
    DELETED(HttpStatus.OK, true, "S004", "리소스가 성공적으로 삭제되었습니다."),

    // Security
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, false, "SC001", "아이디 또는 비밀번호가 올바르지 않습니다."),
    AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, false, "SC002", "인증이 필요합니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, false, "SC003", "접근 권한이 없습니다."),
    ACCOUNT_LOCKED(HttpStatus.LOCKED, false, "SC004", "계정이 잠겼습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, false, "SC005", "토큰이 만료되었습니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, false, "SC006", "유효하지 않은 토큰입니다."),

    // Request
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, false, "CC001", "잘못된 요청입니다."),
    MISSING_HEADER(HttpStatus.BAD_REQUEST, false, "CC002", "필수 헤더가 누락되었습니다."),

    // Verification
    VERIFICATION_CODE_SENT(HttpStatus.OK, true, "V001", "인증 코드가 발송되었습니다."),
    VERIFICATION_COMPLETED(HttpStatus.OK, true, "V002", "인증이 완료되었습니다."),
    VERIFICATION_EXPIRED(HttpStatus.BAD_REQUEST, false, "V003", "인증 코드가 만료되었습니다."),
    VERIFICATION_INVALID_CODE(HttpStatus.BAD_REQUEST, false, "V004", "인증 코드가 일치하지 않습니다."),
    VERIFICATION_ATTEMPTS_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, false, "V005", "인증 시도 횟수를 초과했습니다."),
    VERIFICATION_REQUIRED(HttpStatus.BAD_REQUEST, false, "V006", "이메일 인증이 필요합니다."),
    VERIFICATION_MAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, false, "V007", "인증 메일 발송에 실패했습니다."),

    // User
    USER_DUPLICATE_EMAIL(HttpStatus.CONFLICT, false, "U001", "이미 사용 중인 이메일입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, false, "U002", "사용자를 찾을 수 없습니다."),
    USER_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, false, "U003", "비밀번호가 일치하지 않습니다."),
    USER_INVALID(HttpStatus.BAD_REQUEST, false, "U004", "유효하지 않은 사용자입니다."),
    USER_DELETED(HttpStatus.GONE, false, "U005", "삭제된 사용자입니다."),
    USER_DUPLICATE_NICKNAME(HttpStatus.CONFLICT, false, "U006", "이미 사용 중인 닉네임입니다."),

    // Profile
    PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, false, "P001", "프로필을 찾을 수 없습니다."),
    PROFILE_SLUG_DUPLICATE(HttpStatus.CONFLICT, false, "P002", "이미 사용 중인 slug입니다."),
    PROFILE_LINK_NOT_FOUND(HttpStatus.NOT_FOUND, false, "P003", "링크를 찾을 수 없습니다."),
    PROFILE_LINK_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, false, "P004", "링크는 최대 20개까지 가능합니다."),
    PROFILE_SOCIAL_NOT_FOUND(HttpStatus.NOT_FOUND, false, "P005", "소셜 링크를 찾을 수 없습니다."),
    PROFILE_SOCIAL_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, false, "P006", "소셜 링크는 최대 8개까지 가능합니다."),
    PROFILE_SOCIAL_DUPLICATE_PLATFORM(HttpStatus.CONFLICT, false, "P007", "이미 등록된 플랫폼입니다."),
    PROFILE_INVALID_FIELD(HttpStatus.BAD_REQUEST, false, "P008", "유효하지 않은 프로필 설정값입니다."),

    // Hot Deal
    HOT_DEAL_NOT_FOUND(HttpStatus.NOT_FOUND, false, "H001", "핫딜을 찾을 수 없습니다."),
    HOT_DEAL_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, false, "H002", "댓글을 찾을 수 없습니다."),
    CANDIDATE_DEAL_NOT_FOUND(HttpStatus.NOT_FOUND, false, "H003", "후보 딜을 찾을 수 없습니다."),
    CANDIDATE_DEAL_INVALID_STATUS(HttpStatus.CONFLICT, false, "H004", "승인 또는 거부 처리할 수 없는 상태입니다."),
    HOT_DEAL_DUPLICATE_URL(HttpStatus.CONFLICT, false, "H005", "이미 등록된 URL의 핫딜입니다."),

    // Upload
    UPLOAD_FILE_EMPTY(HttpStatus.BAD_REQUEST, false, "F001", "파일이 비어있습니다."),
    UPLOAD_FILE_TOO_LARGE(HttpStatus.BAD_REQUEST, false, "F002", "파일 크기를 초과합니다."),
    UPLOAD_INVALID_TYPE(HttpStatus.BAD_REQUEST, false, "F003", "지원하지 않는 파일 형식입니다."),

    // Server
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, false, "E001", "서버 내부 오류가 발생했습니다."),
    ;

    companion object {
        fun findByCode(code: String): ApiResponseCode? =
            entries.find { it.code == code }
    }
}
