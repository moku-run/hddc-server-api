# Hexagonal Architecture Guide

## 디렉토리 구조

```
domains/{domain}/
├── domain/              # 핵심 비즈니스 규칙 (가장 안쪽)
│   ├── model/           # 도메인 모델 (data class)
│   ├── value/           # 값 객체 (value class)
│   ├── spec/            # 도메인 상수, 정책 규칙 (object)
│   └── policy/          # 값 생성 정책 (object)
├── application/         # 유스케이스 오케스트레이션 (중간)
│   ├── ports/
│   │   ├── input/       # 인바운드 포트 (XxxUsecase 인터페이스)
│   │   │   ├── command/ # 쓰기 유스케이스
│   │   │   ├── query/   # 읽기 유스케이스
│   │   │   └── validator/ publish/ subscribe/
│   │   └── out/         # 아웃바운드 포트 (XxxPort 인터페이스)
│   │       ├── command/ # DB 쓰기
│   │       ├── query/   # DB 읽기
│   │       ├── guard/   # 인가 (XxxGuard) — requireXxx(), throw, void
│   │       ├── checker/ # 상태 확인 (XxxChecker) — isXxx()/hasXxx(), no throw, Boolean
│   │       ├── validator/ # 비즈니스 규칙 검증 (XxxValidator) — validateXxx(), throw, void
│   │       └── publish/ security/
│   └── service/         # 유스케이스 구현체 (XxxService)
│       ├── command/
│       ├── query/
│       └── validator/ publish/
└── adapter/             # 인프라 연결 (가장 바깥)
    ├── input/           # 인바운드 어댑터
    │   ├── rest/        # REST 컨트롤러 (XxxApi)
    │   │   ├── command/ # 쓰기 API
    │   │   ├── query/   # 읽기 API
    │   │   └── request/ # 요청 DTO
    │   ├── socket/      # WebSocket 핸들러
    │   └── event/       # Kafka 리스너
    └── out/             # 아웃바운드 어댑터
        ├── command/     # 쓰기 어댑터 (XxxCommandAdapter)
        ├── query/       # 읽기 어댑터 (XxxQueryAdapter)
        ├── guard/       # 인가 어댑터 (XxxGuardAdapter)
        ├── checker/     # 상태 확인 어댑터 (XxxCheckerAdapter)
        ├── validator/   # 검증 어댑터 (XxxValidationAdapter)
        ├── publish/     # 이벤트 발행 어댑터
        └── infrastructure/
            ├── r2dbc/
            │   ├── entity/     # R2DBC 엔티티
            │   └── repository/ # R2DBC 리포지토리
            ├── mongo/
            │   ├── doc/        # MongoDB 도큐먼트
            │   └── repository/ # MongoDB 리포지토리
            └── jooq/           # jOOQ 쿼리 클래스
```

## 의존성 규칙

```
        ┌─────────────────────────────────────────────┐
        │                  adapter/input               │  ← 가장 바깥 (REST, WebSocket, Kafka)
        │                  (Controller)                │
        └──────────────────────┬──────────────────────┘
                               │ 의존 (Input 포트 인터페이스)
        ┌──────────────────────▼──────────────────────┐
        │              application/ports/input         │  ← 인바운드 포트 인터페이스
        │                  (XxxUsecase)                │
        └──────────────────────┬──────────────────────┘
                               │ 구현
        ┌──────────────────────▼──────────────────────┐
        │              application/service             │  ← 유스케이스 구현 (오케스트레이션)
        │                  (XxxService)                │
        └──────────────────────┬──────────────────────┘
                               │ 의존 (Output 포트 인터페이스)
        ┌──────────────────────▼──────────────────────┐
        │              application/ports/out           │  ← 아웃바운드 포트 인터페이스
        │          (XxxPort, XxxGuard, XxxValidator)   │
        └──────────────────────┬──────────────────────┘
                               │ 구현
        ┌──────────────────────▼──────────────────────┐
        │                  adapter/out                 │  ← 가장 바깥 (DB, 외부 API)
        │             (XxxAdapter, Entity)             │
        └─────────────────────────────────────────────┘

        ┌─────────────────────────────────────────────┐
        │                   domain/                    │  ← 가장 안쪽 (모두가 참조, 아무것도 참조 안 함)
        │          (Model, Value, Spec, Policy)        │
        └─────────────────────────────────────────────┘
```

### DO

```kotlin
// Service → Output 포트 인터페이스만 의존
@Service
class SaveDirectMessageService(
    private val r2dbcOperator: TransactionalOperator,
    private val directMessageGuard: DirectMessageGuard,
    private val commandDirectMessagePort: CommandDirectMessagePort,
    private val upsertTagUsecase: UpsertTagDirectMessageUsecase,
) : SaveDirectMessageUsecase {

    override fun save(
        auth: MobileUserAuthenticationDTO,
        roomId: MessageRoomId,
        sendMessageModel: SendMessageModel
    ): Mono<MessageModel> =
        throwIf(auth.userRole == UserRole.GUEST, ApiResponseCode.GUEST_INVALID_INVALID_ROLE)
            .then(directMessageGuard.requireMemberOfMessageRoom(auth, roomId))
            .then(directMessageGuard.requireNotRemovedRoom(auth, roomId))
            .then(commandDirectMessagePort.save(auth, sendMessageModel))
            .flatMap { savedMessage ->
                r2dbcOperator
                    .transactional(
                        upsertTagInput
                            .upsert(auth, savedMessage.messageId, roomId, sendMessageModel.tagList, auth.id)
                            .thenReturn(savedMessage)
                    )
                    .onErrorResume { error ->
                        commandDirectMessagePort
                            .removeById(savedMessage.messageId)
                            .then(Mono.error(error))
                    }
            }
}
```

```kotlin
// Controller → Input 포트 인터페이스(Usecase)만 의존
@Tag(name = "201. 회원 가입 API", description = "회원 가입 API")
@RestController
class IndividualSignUpApi(
    private val individualSignUpUsecase: IndividualSignUpUsecase,
) {
    @Operation(summary = "개인 회원가입 API")
    @PostMapping("/public/sign-up/individual")
    fun getSignUp(
        @RequestBody request: IndividualSignUpRequest.SignUp
    ): Mono<ResponseEntity<ApiResponse<String>>> =
        individualSignUpUsecase
            .execute(request.convert)
            .thenReturn(ApiResponse.success(payload = "회원가입에 성공했습니다."))
}
```

```kotlin
// 다른 도메인 → Input 포트 인터페이스(Usecase)로만 호출
@Service
class IndividualSignUpService(
    private val transactionalOperator: TransactionalOperator,
    private val passwordValidator: PasswordValidator,
    private val identityAuthenticationValidator: IdentityAuthenticationValidator,
    private val identityAuthenticationCommandPort: IdentityAuthenticationCommandPort,
    private val userCommandPort: UserCommandPort,
    private val userUniqueValidator: UserUniqueValidator,
    private val commandTermsUsecase: CommandTermsUsecase,       // terms 도메인의 Usecase 포트
    private val applicationEventPublisher: ApplicationEventPublisher,
) : IndividualSignUpUsecase {

    override fun execute(model: IndividualSignUpModel): Mono<Void> =
        validate(model)
            .then(save(model))
            .doOnSuccess { applicationEventPublisher.publishEvent(model) }

    private fun validate(model: IndividualSignUpModel): Mono<Void> =
        passwordValidator.validatePasswordPattern(model.password)
            .then(passwordValidator.validatePasswordMatch(model.password, model.passwordConfirm))
            .then(identityAuthenticationValidator.requireVerified(model.signUpKey))
            .then(userUniqueValidator.validateDuplicateEmail(model.email))

    private fun save(model: IndividualSignUpModel): Mono<Void> =
        transactionalOperator
            .transactional(
                userCommandPort
                    .save(model)
                    .flatMap { commandTermsUsecase.saveTermsWhenIndividualSignUp(it, model.termsList) }
            )
            .then(identityAuthenticationCommandPort.remove(model.signUpKey))
}
```

### DON'T

```kotlin
// Service에서 Adapter 구현체를 직접 참조 (위반)
class SaveDirectMessageService(
    private val guardAdapter: DirectMessageGuardAdapter,         // 구현체 직접 참조 금지!
    private val repository: DirectMessageDocumentRepository,     // 리포지토리 직접 참조 금지!
) { ... }

// 다른 도메인의 Service 구현체를 직접 참조 (위반)
class IndividualSignUpService(
    private val termsService: SaveTermsService,  // 구현체가 아니라 Usecase 포트를 사용해야 함!
) { ... }

// domain 패키지에서 adapter 패키지 import (위반)
package kr.co.hiveworks.domains.user.domain.model
import kr . co . hiveworks . domains . user . adapter . out . infrastructure . r2dbc . entity . MstUserEntity // 절대 금지!
```

## 네이밍 컨벤션

| 계층                      | 역할          | 접미사                             | 예시                          |
|-------------------------|-------------|---------------------------------|-----------------------------|
| Input Port              | 유스케이스 인터페이스 | `XxxUsecase`                    | `IndividualSignUpUsecase`   |
| Service                 | 유스케이스 구현    | `XxxService`                    | `IndividualSignUpService`   |
| Output Port (command)   | DB 쓰기 포트    | `XxxCommandPort`                | `UserCommandPort`           |
| Output Port (query)     | DB 읽기 포트    | `XxxQueryPort`                  | `UserQueryPort`             |
| Output Port (guard)     | 인가 포트       | `XxxGuard`                      | `DirectMessageGuard`        |
| Output Port (validator) | 검증 포트       | `XxxValidator`                  | `UserValidator`             |
| Output Port (publisher) | 이벤트 발행 포트   | `XxxPublisher`                  | `ProfileChangePublisher`    |
| Command Adapter         | DB 쓰기 구현    | `XxxCommandAdapter`             | `UserCommandAdapter`        |
| Query Adapter           | DB 읽기 구현    | `XxxQueryAdapter`               | `UserQueryAdapter`          |
| Guard Adapter           | 인가 구현       | `XxxGuardAdapter`               | `DirectMessageGuardAdapter` |
| Validation Adapter      | 검증 구현       | `XxxValidationAdapter`          | `UserValidationAdapter`     |
| Controller              | REST API    | `XxxApi`                        | `IndividualSignUpApi`       |
| Request DTO             | 요청          | `XxxRequest` (sealed interface) | `IndividualSignUpRequest`   |

## Guard vs Checker vs Validator 구분

|             | Guard (인가)                                 | Checker (상태 확인)                | Validator (검증)                  |
|-------------|--------------------------------------------|---------------------------------|---------------------------------|
| **질문**      | "이 사용자가 이 작업을 할 **권한**이 있는가?"              | "이 **상태**인가?"                   | "이 데이터가 **유효**한가?"              |
| **auth 필요** | 항상 `auth` 파라미터 필요                          | 불필요                             | 불필요 (데이터 상태만 확인)               |
| **메서드명**    | `requireXxx()`                             | `isXxx()`, `hasXxx()`           | `validateXxx()`                 |
| **throw**    | 던짐                                         | **안 던짐**                        | 던짐                              |
| **반환 타입**   | `void`                                     | `Boolean`                       | `void`                          |
| **예시**      | `requireMemberOfRoom(auth, roomId)` | `isExpired(dealId)`, `hasPermission(userId)` | `validateDuplicateEmail(email)` |
| **위치**      | `ports/out/guard/`                         | `ports/out/checker/`            | `ports/out/validator/`          |

```kotlin
// Guard: "이 사용자가 이 채팅방의 멤버인가?" (인가, throw, void)
interface DirectMessageGuard {
    fun requireMemberOfMessageRoom(auth: UserAuthenticationDTO, roomId: Long)
    fun requireAuthor(auth: UserAuthenticationDTO, messageId: Long)
    fun requireNotRemovedRoom(auth: UserAuthenticationDTO, roomId: Long)
}
```

```kotlin
// Checker: "이 상태인가?" (no throw, Boolean 반환)
interface DealChecker {
    fun isExpired(dealId: Long): Boolean
    fun hasLiked(userId: Long, dealId: Long): Boolean
}
```

```kotlin
// Validator: "이 데이터가 유효한가?" (throw, void)
interface PasswordValidator {
    fun validatePasswordPattern(password: String)
    fun validatePasswordMatch(password: String, confirm: String)
}

interface UserValidationPort {
    fun requireEmailNotExists(email: String)
    fun requireNicknameNotExists(nickname: String)
}
```

## 크로스 도메인 규칙

- 다른 도메인의 기능을 사용할 때는 **반드시 Input 포트 인터페이스**를 통해서만 접근한다
- adapter 간 직접 참조 (예: A 도메인 adapter → B 도메인 repository) 금지
