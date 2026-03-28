# Application Layer Guide

application 계층은 비즈니스 유스케이스를 오케스트레이션한다. **포트 인터페이스**로 내부/외부 경계를 정의하고, **유스케이스**에서 포트를 조합한다.

## 핵심 규칙

1. **UseCase는 포트 인터페이스만 의존** — adapter 구현체 직접 참조 금지
2. **UseCase는 오케스트레이션만** — 비즈니스 로직은 domain에, 인프라 로직은 adapter에
3. **동기 스타일** — Spring Boot + VirtualThread 모델
4. **크로스 도메인은 Input 포트만** — 다른 도메인의 Usecase 구현체 직접 참조 금지
5. **Spring Data `Pageable`/`Page` 허용** — 페이징 타입은 application 계층 Port에서 사용 가능

---

## 1. Input Port (`application/ports/input/`)

Input 포트는 유스케이스의 **계약(contract)**을 정의하는 인터페이스다. Controller가 이 인터페이스에 의존한다.

### 규칙

- 인터페이스명: `XxxUsecase`
- 메서드: command는 `execute(model)`, query는 서술적 동사 (`find`, `fetch`, `search`)
- 반환: `Mono<T>` (단건) 또는 `Flux<T>` (다건). command는 보통 `Mono<Void>`
- 파라미터: **domain 모델만** 받는다 (Request DTO 금지)

### DO

```kotlin
// 단일 책임: 하나의 유스케이스 = 하나의 인터페이스
interface IndividualSignUpInput {
    fun execute(model: IndividualSignUpModel): Mono<Void>
}

// command 인터페이스: 인가 대상에 auth 추가 가능
interface SaveDirectMessageInput {
    fun save(
        auth: MobileUserAuthenticationDTO,
        roomId: MessageRoomId,
        sendMessageModel: SendMessageModel,
    ): Mono<MessageModel>
}

// query 인터페이스: 서술적 메서드명
interface FetchDirectMessageRoomInput {
    fun findAllByUserId(auth: MobileUserAuthenticationDTO): Mono<List<DirectMessageRoomModel>>
}
```

### DON'T

```kotlin
// 금지: Request DTO를 Input 포트에 노출
interface SignUpInput {
    fun execute(request: IndividualSignUpRequest.SignUp): Mono<Void>  // Request DTO 금지!
}

// 금지: 하나의 인터페이스에 여러 유스케이스
interface UserInput {
    fun signUp(model: SignUpModel): Mono<Void>
    fun login(model: LoginModel): Mono<Void>   // 분리해야 함!
    fun delete(userId: Long): Mono<Void>        // 분리해야 함!
}
```

---

## 2. Output Port (`application/ports/out/`)

Output 포트는 유스케이스가 인프라(DB, 외부 API)에 접근하기 위한 인터페이스다. Adapter가 이 인터페이스를 구현한다.

### 2.1 Command Port

```kotlin
// 같은 aggregate의 쓰기 연산을 하나의 포트에 그룹핑
interface UserCommandPort {
    fun save(model: CreateUserModel): Mono<Long>
    fun save(model: IndividualSignUpModel): Mono<Long>

    fun update(userId: Long, model: UpdateUserModel): Mono<Void>
    fun update(userId: Long, model: UpdateUserProfileModel): Mono<Void>
    fun update(userId: Long, model: UpdateMobileUserProfileModel): Mono<Void>
    fun update(userId: Long, model: UpdateGuestProfileModel): Mono<Void>
    fun update(userId: Long, model: UpdatePasswordModel): Mono<Void>
    fun update(userId: Long, model: UpdateEmailModel): Mono<Void>

    fun delegateAdmin(adminId: Long, targetId: Long): Mono<Void>

    fun delete(userIdList: List<Long>): Mono<Void>

    fun removeMembersFromCompany(userIdList: List<Long>): Mono<Void>
}
```

- 메서드 오버로딩으로 모델 타입별 분기 (타입이 계약)
- 반환: 생성은 `Mono<Long>` (ID), 수정/삭제는 `Mono<Void>`

### 2.2 Query Port

```kotlin
interface UserQueryPort {
    fun findById(userId: Long): Mono<UserModel>
    fun findAllByCompanyId(companyId: Long): Mono<List<UserModel>>
}
```

- finder 메서드는 `Mono<T>`를 반환 (not-found → `Mono.empty()`)
- `Mono.empty()` 처리는 adapter에서 `switchIfEmpty(Mono.error(...))` 로 수행

### 2.3 Guard Port (인가)

```kotlin
// "이 사용자가 이 작업을 할 권한이 있는가?" — requireXxx(), throw, void
interface DirectMessageGuard {
    fun requireMemberOfMessageRoom(auth: UserAuthenticationDTO, roomId: Long)
    fun requireAuthor(auth: UserAuthenticationDTO, messageId: Long)
    fun requireNotRemovedRoom(auth: UserAuthenticationDTO, roomId: Long)
}
```

- **항상 `auth` 파라미터 필요** — 인가 판단의 기준은 "누가" 요청했는가
- 메서드명: `requireXxx()`
- 실패 시 throw, 반환: `void`

### 2.4 Checker Port (상태 확인)

```kotlin
// "이 상태인가?" — isXxx(), hasXxx(), no throw, Boolean 반환
interface DealChecker {
    fun isExpired(dealId: Long): Boolean
    fun hasLiked(userId: Long, dealId: Long): Boolean
}
```

- **throw 안 던짐** — 호출자가 결과를 보고 판단
- 메서드명: `isXxx()`, `hasXxx()`
- 반환: `Boolean`

### 2.5 Validator Port (비즈니스 규칙 검증)

```kotlin
// "이 데이터가 유효한가?" — validateXxx(), throw, void
interface PasswordValidator {
    fun validatePasswordPattern(password: String)
    fun validatePasswordMatch(password: String, confirm: String)
}

interface UserValidationPort {
    fun requireEmailNotExists(email: String)
    fun requireNicknameNotExists(nickname: String)
}
```

- **auth 없이 데이터 상태만 확인** — Guard와의 핵심 차이
- 실패 시 throw, 반환: `void`

### 2.6 Publisher Port (이벤트 발행)

```kotlin
interface ProfileChangePublisher {
    fun publish(userId: Long, profileModel: UserProfileModel): Mono<Void>
}
```

---

## 3. UseCase (`application/service/`)

UseCase는 Input 포트를 구현하고, Output 포트를 조합하여 비즈니스 플로우를 오케스트레이션한다.

### 기본 구조: 검증 → 저장 → 사이드이펙트

```kotlin
@Service
class IndividualSignUpService(
    private val transactionalOperator: TransactionalOperator,
    private val passwordValidator: PasswordValidator,
    private val identityAuthenticationValidator: IdentityAuthenticationValidator,
    private val identityAuthenticationCommandPort: IdentityAuthenticationCommandPort,
    private val userCommandPort: UserCommandPort,
    private val userUniqueValidator: UserUniqueValidator,
    private val commandTermsUsecase: CommandTermsUsecase,
    private val applicationEventPublisher: ApplicationEventPublisher,
) : IndividualSignUpInput {

    override fun execute(model: IndividualSignUpModel): Mono<Void> =
        validate(model)                  // 1. 검증
            .then(save(model))           // 2. 저장
            .doOnSuccess { applicationEventPublisher.publishEvent(model) }  // 3. 이벤트

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

### UseCase 실행 흐름 패턴

```
execute(model)
  │
  ├── 1. validate(model)              ← throwIf / Validator / Guard 체인
  │     ├── throwIf(precondition)     ← 인라인 조건 검사
  │     ├── guard.require*(auth, ...)  ← 인가 검사
  │     └── validator.validate*(...)   ← 비즈니스 규칙 검사
  │
  ├── 2. save(model)                  ← 트랜잭션 내 쓰기 연산
  │     └── transactionalOperator.transactional(
  │           port.save(...)
  │               .flatMap { crossDomainInput.save(...) }
  │         )
  │
  └── 3. side effects                 ← doOnSuccess로 이벤트/알림
        ├── applicationEventPublisher.publishEvent(...)
        └── publisher.publish(...)
```

### Guard + 보상 패턴을 포함한 UseCase

```kotlin
@Service
class SaveDirectMessageService(
    private val r2dbcOperator: TransactionalOperator,
    private val directMessageGuard: DirectMessageGuard,
    private val commandDirectMessagePort: CommandDirectMessagePort,
    private val upsertTagInput: UpsertTagDirectMessageInput,
) : SaveDirectMessageInput {

    override fun save(
        auth: MobileUserAuthenticationDTO,
        roomId: MessageRoomId,
        sendMessageModel: SendMessageModel
    ): Mono<MessageModel> =
        // 1. 인라인 Guard: DB 조회 없이 판단 가능한 단순 조건
        throwIf(auth.userRole == UserRole.GUEST, ApiResponseCode.GUEST_INVALID_INVALID_ROLE)
            // 2. Guard 포트: DB 조회가 필요한 인가
            .then(directMessageGuard.requireMemberOfMessageRoom(auth, roomId))
            .then(directMessageGuard.requireNotRemovedRoom(auth, roomId))
            // 3. MongoDB 저장 (R2DBC 트랜잭션 밖)
            .then(commandDirectMessagePort.save(auth, sendMessageModel))
            .flatMap { savedMessage ->
                // 4. PostgreSQL 저장 (R2DBC 트랜잭션 안)
                r2dbcOperator
                    .transactional(
                        upsertTagInput
                            .upsert(auth, savedMessage.messageId, roomId, sendMessageModel.tagList, auth.id)
                            .thenReturn(savedMessage)
                    )
                    // 5. 보상: PostgreSQL 실패 시 MongoDB 문서 삭제
                    .onErrorResume { error ->
                        commandDirectMessagePort
                            .removeById(savedMessage.messageId)
                            .then(Mono.error(error))
                    }
            }
}
```

### 인라인 Guard: throwIf / throwUnless

간단한 역할 체크는 Guard 포트 없이 UseCase에서 직접 처리한다.

```kotlin
// 리액티브 조건 검사 유틸리티
fun throwIf(condition: Boolean, apiResponseCode: ApiResponseCode): Mono<Void> =
    if (condition) Mono.error(ApiException(apiResponseCode)) else Mono.empty()

fun throwUnless(condition: Boolean, apiResponseCode: ApiResponseCode): Mono<Void> =
    if (!condition) Mono.error(ApiException(apiResponseCode)) else Mono.empty()
```

- DB 조회 없이 판단 가능한 단순 조건 → `throwIf`/`throwUnless`
- DB 조회가 필요한 복잡한 조건 → Guard/Validator 포트

### 트랜잭션 패턴

```kotlin
// R2DBC 트랜잭션: 쓰기 연산만 감싼다
private fun save(model: IndividualSignUpModel): Mono<Void> =
    transactionalOperator.transactional(
        userCommandPort.save(model)
            .flatMap { commandTermsUsecase.saveTermsWhenIndividualSignUp(it, model.termsList) }
    )
        // 트랜잭션 바깥: 비-트랜잭셔널 연산 (Redis 삭제 등)
        .then(identityAuthenticationCommandPort.remove(model.signUpKey))
```

```kotlin
// MongoDB + PostgreSQL 보상 패턴 (MongoDB는 R2DBC 트랜잭션 밖)
commandDirectMessagePort.save(auth, sendMessageModel)  // MongoDB 저장
    .flatMap { savedMessage ->
        r2dbcOperator.transactional(
            upsertTagInput.upsert(auth, savedMessage.messageId, roomId, ...
        )
            .thenReturn(savedMessage)
        )
        .onErrorResume { error ->
        // PostgreSQL 실패 시 MongoDB 보상 삭제
        commandDirectMessagePort.removeById(savedMessage.messageId)
            .then(Mono.error(error))
    }
    }
```

### DON'T: UseCase 안티패턴

```kotlin
// 금지: UseCase에서 직접 비밀번호 인코딩 (adapter의 역할!)
class SaveMessageService(...) {
    override fun execute(...) {
        val encoded = passwordEncoder.encode(model.password)  // 금지!
    }
}

// 금지: UseCase에서 Entity/Document 직접 생성 (adapter의 역할!)
class SaveMessageService(...) {
    override fun execute(...) {
        val entity = MstUserEntity(...)  // 금지!
        repository.save(entity)          // port를 통해야 함!
    }
}

// 금지: .block() 사용
class SomeService(...) {
    override fun execute(...): Mono<Void> {
        val user = userQueryPort.findById(userId).block()  // 절대 금지!
    }
}

// 금지: @Transactional + TransactionalOperator 동시 사용
@Transactional  // 이미 클래스 레벨 트랜잭션이 있으면
class SomeService(...) {
    fun save() = transactionalOperator.transactional(...)  // 이중 감싸기 금지!
}

// 금지: 도메인 이벤트에 기술명 포함
class DealSseEvent(...)    // 금지! "SSE"는 기술 관심사
class DealEvent(...)       // 올바름. 비즈니스 이벤트

// 금지: 서비스에서 ! 연산자로 부정 판단
if (!deal.isActive) return null   // 금지!
if (deal.isInactive) return null  // 올바름. 긍정형 프로퍼티 사용
```
