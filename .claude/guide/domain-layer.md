# Domain Layer Guide

domain 계층은 헥사고날 아키텍처의 가장 안쪽 레이어다. 외부 의존성 없이 순수 Kotlin으로 작성하며, 비즈니스 규칙과 정책을 표현한다.

## 핵심 규칙

1. **Spring 어노테이션 사용 금지** — `@Component`, `@Entity`, `@Document`, `@Table` 등 일체 금지
2. **인프라 라이브러리 import 금지** — R2DBC, MongoDB, jOOQ, Reactor 등 import 금지
3. **다른 도메인의 domain은 참조 가능** — 같은 계층 간 참조는 허용
4. **adapter/application 역참조 금지** — domain에서 상위 계층을 import하면 안 됨

---

## 1. Domain Model (`domain/model/`)

도메인 모델은 비즈니스 의미를 담는 데이터 캐리어다. `data class`로 작성하며, 파생 값이나 비즈니스 규칙을 computed property로 표현할 수 있다.

### DO: 비즈니스 규칙을 모델에 표현

```kotlin
data class IndividualSignUpModel(
    val email: String,
    val name: String,
    val password: String,
    val passwordConfirm: String,
    val companyName: String?,
    val department: String?,
    val job: String?,
    val phoneHead: String,
    val phoneMid: String,
    val phoneTail: String,
    val termsList: List<CommandTermsModel>
) {
    // 파생 값: 전화번호 조합 (비즈니스 로직)
    private val phoneFull get() = "$phoneHead$phoneMid$phoneTail"

    // 비즈니스 규칙: 개인 회원가입의 초기 역할은 항상 GUEST
    val role = UserRole.GUEST

    // 도메인 Spec 참조를 통한 키 생성 (다른 도메인의 Spec 참조 가능)
    val signUpKey get() = IdentityAuthenticationSpec.generateSignUpKey(phoneFull)
}
```

### DON'T: domain 모델에 인프라 코드 포함

```kotlin
// 금지: Spring 어노테이션 사용
@Document(collection = "messages")  // 금지!
data class MessageModel(...)

// 금지: Reactor 타입을 모델 필드로
data class UserModel(
    val name: Mono<String>  // 금지! Mono는 application/adapter 레이어에서만
)

// 금지: 인프라 의존
import org . springframework . data . annotation . Id  // 금지!
        import org . springframework . security . crypto . password . PasswordEncoder  // 금지!
```

### 모델 유형

| 유형               | 용도         | 예시                                                             |
|------------------|------------|----------------------------------------------------------------|
| Command Model    | 쓰기 요청 데이터  | `IndividualSignUpModel`, `CreateUserModel`, `SendMessageModel` |
| Fetch/Read Model | 조회 응답 데이터  | `UserModel`, `FetchUserModel`, `MessageModel`                  |
| Event Model      | 이벤트 전달 데이터 | `KafkaEventModel`                                              |

---

## 2. Value Object (`domain/value/`)

값 객체는 `@JvmInline value class`로 작성한다. 타입 안전성을 제공하면서 런타임 오버헤드가 없다.

### DO: 풍부한 비즈니스 의미를 값 객체에 표현

```kotlin
@JvmInline
value class MessageRoomId private constructor(
    val value: UUID
) {
    // 파생 값: 채널 코드 (비즈니스 규칙)
    val valueToString get() = value.toString()
    val dmChannelCode get() = ChannelType.DIRECT_MESSAGE.prefix + value
    val gmChannelCode get() = ChannelType.GROUP_MESSAGE.prefix + value
    val emChannelCode get() = ChannelType.EXTERNAL_MESSAGE.prefix + value

    val dmViewInChannelCode get() = OperationType.VIEW_IN_MESSAGE_ROOM.name + ChannelType.DIRECT_MESSAGE.prefix + value
    val gmViewInChannelCode get() = OperationType.VIEW_IN_MESSAGE_ROOM.name + ChannelType.GROUP_MESSAGE.prefix + value
    val emViewInChannelCode get() = OperationType.VIEW_IN_MESSAGE_ROOM.name + ChannelType.EXTERNAL_MESSAGE.prefix + value

    // 스마트 생성자: companion object factory
    companion object {
        fun init() = MessageRoomId(MessageIdCreator.create())  // 새로 생성
        fun of(value: String) = MessageRoomId(UUID.fromString(value))  // 문자열에서 파싱
        fun of(value: UUID) = MessageRoomId(value)  // UUID 래핑
    }
}
```

```kotlin
@JvmInline
value class CompanyCode private constructor(
    val value: String
) {
    companion object {
        // Policy 객체에 위임하여 생성
        fun init(): CompanyCode = CompanyCode(CompanyCodeGenerator.generate())
    }
}
```

### DO: 단순 ID 래핑

```kotlin
@JvmInline
value class CompanyId(val value: Long)
```

### Value Object 패턴 정리

| 패턴          | 생성자     | factory             | 예시                             |
|-------------|---------|---------------------|--------------------------------|
| **단순 래핑**   | public  | 없음                  | `CompanyId(value: Long)`       |
| **스마트 생성자** | private | `init()`, `of(...)` | `MessageRoomId`, `CompanyCode` |

- private 생성자 + companion factory를 사용하면 생성 로직을 통제할 수 있다
- `init()`은 새로운 값 생성, `of()`는 기존 값 파싱/래핑

---

## 3. Spec (`domain/spec/`)

Spec은 `object` 싱글턴으로, 도메인 상수와 검증 규칙을 정의한다. 모델과 adapter 양쪽에서 참조된다.

### DO: 상수와 패턴을 Spec에 집중

```kotlin
object UserPasswordSpec {
    const val NOT_EMPTY_MESSAGE = "비밀번호는 필수입니다."

    const val MIN_LENGTH = 8
    const val INVALID_MIN_LENGTH_MESSAGE = "비밀번호는 최소 ${MIN_LENGTH}글자 이상이어야 합니다."

    const val MAX_LENGTH = 20
    const val INVALID_MAX_LENGTH_MESSAGE = "비밀번호는 최대 ${MAX_LENGTH}글자까지 허용됩니다."

    const val PATTERN_STRING = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_=+.])(?!.*\\s).{8,20}$"
    const val INVALID_PATTERN_MESSAGE =
        "비밀번호는 영문, 숫자, 특수 문자 3종류 조합이 필수입니다."
    val PATTERN = Regex(PATTERN_STRING)
}
```

### DON'T: Spec에 인프라 의존성

```kotlin
// 금지: Spec에서 Redis/DB 등 참조
object UserSpec {
    fun findUser(repository: MstUserRepository) = ...  // 금지! Spec은 순수해야 함
}
```

---

## 4. Policy (`domain/policy/`)

Policy는 `object` 싱글턴으로, 값 생성 알고리즘을 캡슐화한다. Spec과 달리 **동적인 값을 생성**한다.

```kotlin
object CompanyCodeGenerator {
    private val random = SecureRandom()

    fun generate(): String = (1..8).map { random.nextInt(10) }.joinToString("")
}
```

```kotlin
object MessageIdCreator {
    fun create(): UUID = Generators.timeBasedEpochGenerator().generate()
}
```

---

## 5. 비즈니스 로직을 domain에 올리는 패턴

### 원칙

application/adapter 계층에 있는 로직 중 **DB/Redis/외부 API 조회 없이 순수하게 판단 가능한 로직**은 domain으로 올린다.
리액티브 래퍼(`Mono<Void>`)는 application/adapter에 남기되, **boolean 판단 자체**를 domain으로 올리는 것이 핵심이다.

```
domain:       fun isValidPattern(password: String): Boolean   ← 판단 (순수 Kotlin)
adapter:      throwUnless(Spec.isValidPattern(pw), ERROR)     ← 리액티브 래핑 (Reactor)
```

### 판단 기준

| 기준                           | 위치                          | 예시                           |
|------------------------------|-----------------------------|------------------------------|
| DB/Redis 조회 없이 boolean 판단 가능 | **domain으로 올림**             | 비밀번호 패턴, 역할 체크, 중복 검사, 길이 제한 |
| DB/Redis 조회가 필요              | **application/adapter에 유지** | 이메일 중복, 사용자 존재 확인, 인증 상태     |

### 5.1 Enum에 비즈니스 의미 부여

역할 기반 분기는 가장 흔한 비즈니스 로직이다. enum이 판단 메서드를 가지면 UseCase에서 도메인 언어로 읽힌다.

```kotlin
// Before: 빈 enum, UseCase 15개+에서 == 비교 반복
enum class UserRole {
    ADMIN(),
    MEMBER(),
    GUEST(),
}

// UseCase에서
throwIf(auth.userRole == UserRole.GUEST, ApiResponseCode.GUEST_INVALID_INVALID_ROLE)

// Validator에서
throwUnless(auth.userRole in listOf(UserRole.ADMIN), ApiResponseCode.ACCESS_DENIED)
```

```kotlin
// After: enum이 비즈니스 판단을 가짐
enum class UserRole {
    ADMIN,
    MEMBER,
    GUEST;

    val isAdmin: Boolean get() = this == ADMIN
    val isMember: Boolean get() = this == MEMBER
    val isGuest: Boolean get() = this == GUEST

    /** 회사에 소속된 역할인가 (ADMIN, MEMBER) */
    val isCompanyMember: Boolean get() = this == ADMIN || this == MEMBER

    /** 관리 권한(멤버 초대, 삭제 등)을 가지는가 */
    val hasManagementAuthority: Boolean get() = this == ADMIN
}

// UseCase — 도메인 언어로 읽힘
throwIf(auth.userRole.isGuest, ApiResponseCode.GUEST_INVALID_INVALID_ROLE)

// Validator — 의도가 명확
throwUnless(auth.userRole.hasManagementAuthority, ApiResponseCode.ACCESS_DENIED)
```

### 5.2 Spec에 검증 로직 추가

Spec은 상수만 정의하는 것이 아니라, **그 상수를 기반으로 한 판단 메서드**도 가져야 한다.

```kotlin
// Before: Spec에 상수만, 판단은 adapter에 산재
object UserPasswordSpec {
    const val PATTERN_STRING = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_=+.])(?!.*\\s).{8,20}$"
    val PATTERN = Regex(PATTERN_STRING)
}

// adapter에서 판단
@Component
class PasswordValidationAdapter : PasswordValidator {
    override fun validatePasswordMatch(password: String, passwordConfirm: String): Mono<Void> =
        throwUnless(password == passwordConfirm, ApiException(ApiResponseCode.PASSWORD_MISMATCH))

    override fun validatePasswordPattern(password: String): Mono<Void> =
        throwUnless(UserPasswordSpec.PATTERN.matches(password), ApiException(ApiResponseCode.INVALID_PASSWORD))
}
```

```kotlin
// After: Spec이 판단 로직을 가짐
object UserPasswordSpec {
    const val MIN_LENGTH = 8
    const val MAX_LENGTH = 20
    const val PATTERN_STRING = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_=+.])(?!.*\\s).{8,20}$"
    val PATTERN = Regex(PATTERN_STRING)

    /** 비밀번호가 패턴 규칙에 부합하는가 */
    fun isValidPattern(password: String): Boolean = PATTERN.matches(password)

    /** 비밀번호와 확인이 일치하는가 */
    fun isMatched(password: String, confirm: String): Boolean = password == confirm
}

// adapter — domain에 위임
@Component
class PasswordValidationAdapter : PasswordValidator {
    override fun validatePasswordMatch(password: String, passwordConfirm: String): Mono<Void> =
        throwUnless(
            UserPasswordSpec.isMatched(password, passwordConfirm),
            ApiException(ApiResponseCode.PASSWORD_MISMATCH)
        )

    override fun validatePasswordPattern(password: String): Mono<Void> =
        throwUnless(UserPasswordSpec.isValidPattern(password), ApiException(ApiResponseCode.INVALID_PASSWORD))
}
```

### 5.3 Notice Spec — 복붙 로직 제거

같은 검증 로직이 여러 UseCase에 복붙되어 있으면, 판단을 Spec으로 올려서 중복을 제거한다.

```kotlin
// Before: 6개 UseCase(CreateDm/Gm/Em, UpdateDm/Gm/Em)에 동일한 private 함수가 복붙
object DmNoticeSpec {
    const val MAX_TITLE_LENGTH = 100
    const val MAX_CONTENT_LENGTH = 5000
}

// CreateDmNoticeUsecase, UpdateDmNoticeUsecase, CreateGmNoticeUsecase... 에 복붙
private fun validateTitleAndContent(title: String, content: String): Mono<Void> =
    throwIf(
        title.isBlank() || title.length > DmNoticeSpec.MAX_TITLE_LENGTH,
        ApiResponseCode.DM_NOTICE_INVALID_TITLE,
    ).then(
        throwIf(
            content.isBlank() || content.length > DmNoticeSpec.MAX_CONTENT_LENGTH,
            ApiResponseCode.DM_NOTICE_INVALID_CONTENT,
        )
    )
```

```kotlin
// After: Spec이 판단, UseCase는 위임만
object DmNoticeSpec {
    const val MAX_TITLE_LENGTH = 100
    const val MAX_CONTENT_LENGTH = 5000

    fun isValidTitle(title: String): Boolean =
        title.isNotBlank() && title.length <= MAX_TITLE_LENGTH

    fun isValidContent(content: String): Boolean =
        content.isNotBlank() && content.length <= MAX_CONTENT_LENGTH
}

// UseCase — private 함수 제거, Spec에 위임
override fun create(
    auth: MobileUserAuthenticationDTO,
    dmRoomId: MessageRoomId,
    title: String,
    content: String
): Mono<DmNoticeModel> =
    throwIf(auth.userRole.isGuest, ApiResponseCode.GUEST_INVALID_INVALID_ROLE)
        .then(throwUnless(DmNoticeSpec.isValidTitle(title), ApiResponseCode.DM_NOTICE_INVALID_TITLE))
        .then(throwUnless(DmNoticeSpec.isValidContent(content), ApiResponseCode.DM_NOTICE_INVALID_CONTENT))
        .then(directMessageGuard.requireMemberOfMessageRoom(auth, dmRoomId))
        .then(commandDmNoticePort.save(dmRoomId, auth.id, title, content))
        .flatMap { notice -> pushAndPub(auth, dmRoomId, notice).thenReturn(notice) }
```

### 5.4 Spec에 숫자 제한 판단 추가

상수만 있고 판단이 adapter에 있는 Spec은 판단 메서드를 추가한다.

```kotlin
// Before: 상수만 있는 Spec, 판단은 adapter에
class TagLimitSpec {
    companion object {
        const val MAX_COUNT = 3
    }
}

// adapter에서 판단
override fun validateCount(tagIdList: List<Long>): Mono<Void> =
    throwIf(tagIdList.size > TagLimitSpec.MAX_COUNT, ApiResponseCode.TAG_LIMIT_COUNT)
```

```kotlin
// After: Spec이 판단을 가짐
object TagLimitSpec {
    const val MAX_COUNT = 3

    fun exceedsLimit(count: Int): Boolean = count > MAX_COUNT
}

// adapter — domain에 위임
override fun validateCount(tagIdList: List<Long>): Mono<Void> =
    throwIf(TagLimitSpec.exceedsLimit(tagIdList.size), ApiResponseCode.TAG_LIMIT_COUNT)
```

```kotlin
// adapter companion에 숨어있던 상수를 Spec으로 이동
// Before: GroupMessageRoomValidationAdapter
@Service
class GroupMessageRoomValidationAdapter : GroupMessageRoomValidator {
    override fun validateMinParticipants(userIdList: List<Long>): Mono<Void> =
        throwIf(userIdList.size < MIN_PARTICIPANTS, ApiResponseCode.GM_INSUFFICIENT_PARTICIPANTS)

    companion object {
        private const val MIN_PARTICIPANTS = 2  // 비즈니스 규칙이 adapter에 숨어있음!
    }
}

// After: 새 Spec 생성
object GmRoomSpec {
    const val MIN_PARTICIPANTS = 2

    fun hasMinimumParticipants(count: Int): Boolean = count >= MIN_PARTICIPANTS
}

// adapter — Spec에 위임
@Service
class GroupMessageRoomValidationAdapter : GroupMessageRoomValidator {
    override fun validateMinParticipants(userIdList: List<Long>): Mono<Void> =
        throwUnless(GmRoomSpec.hasMinimumParticipants(userIdList.size), ApiResponseCode.GM_INSUFFICIENT_PARTICIPANTS)
}
```

### 5.5 공통 도메인 규칙 유틸리티

여러 도메인에 걸쳐 반복되는 순수 판단 로직은 공통 도메인 규칙으로 추출한다.

```kotlin
// Before: 7개+ 파일에서 동일한 패턴 반복
// MemberInviteValidationAdapter
override fun validateNoDuplicatePhones(phones: List<String>): Mono<Void> =
    throwIf(phones.toSet().size != phones.size, ApiResponseCode.MEMBER_INVITE_DUPLICATE_INVITE)

override fun validateNoDuplicateUserIds(userIds: List<Long>): Mono<Void> =
    throwIf(userIds.toSet().size != userIds.size, ApiResponseCode.MEMBER_INVITE_DUPLICATE_INVITE)

// GroupMessageRoomValidationAdapter
override fun validateNoDuplicateParticipants(userIdList: List<Long>): Mono<Void> =
    throwIf(userIdList.size != userIdList.toSet().size, ApiResponseCode.GM_DUPLICATE_PARTICIPANT)

// TagGuardAdapter
override fun duplicate(tagIdList: List<Long>): Mono<Void> =
    throwUnless(tagIdList.size == tagIdList.toSet().size, ApiResponseCode.TAG_DUPLICATE)

// MemberInviteValidationAdapter
override fun validateMySelf(authId: Long, targetUserId: Long): Mono<Void> =
    throwIf(authId == targetUserId, ApiResponseCode.MEMBER_INVITE_INVALID_MY_SELF)
```

```kotlin
// After: 공통 도메인 규칙 유틸리티
object DomainRule {
    /** 리스트에 중복 요소가 있는가 */
    fun <T> hasDuplicates(list: List<T>): Boolean =
        list.size != list.toSet().size

    /** 동일 사용자인가 (자기 자신 체크) */
    fun isSameUser(userId1: Long, userId2: Long): Boolean =
        userId1 == userId2
}

// 각 adapter — domain에 위임, 의도가 명확
override fun validateNoDuplicatePhones(phones: List<String>): Mono<Void> =
    throwIf(DomainRule.hasDuplicates(phones), ApiResponseCode.MEMBER_INVITE_DUPLICATE_INVITE)

override fun validateNoDuplicateParticipants(userIdList: List<Long>): Mono<Void> =
    throwIf(DomainRule.hasDuplicates(userIdList), ApiResponseCode.GM_DUPLICATE_PARTICIPANT)

override fun duplicate(tagIdList: List<Long>): Mono<Void> =
    throwIf(DomainRule.hasDuplicates(tagIdList), ApiResponseCode.TAG_DUPLICATE)

override fun validateMySelf(authId: Long, targetUserId: Long): Mono<Void> =
    throwIf(DomainRule.isSameUser(authId, targetUserId), ApiResponseCode.MEMBER_INVITE_INVALID_MY_SELF)
```

### 5.7 도메인 모델의 상태 연산 메서드

카운터 증감, 상태 파생 같은 연산은 도메인 모델에 메서드로 표현한다. 서비스에서 인라인 산술(`deal.likeCount + 1`)을 하지 않는다.

```kotlin
// DO: 모델이 연산을 가짐
data class HotDealModel(...) {
    val isActive: Boolean get() = !isDeleted && !isExpired
    val isInactive: Boolean get() = isDeleted || isExpired

    fun incrementedLikeCount(): Int = likeCount + 1
    fun decrementedLikeCount(): Int = maxOf(0, likeCount - 1)
}

// Service — 도메인 언어로 읽힘
val newCount = deal.incrementedLikeCount()
hotDealCommandPort.updateLikeCount(dealId, newCount)
```

```kotlin
// DON'T: 서비스에서 인라인 산술
val newCount = deal.likeCount + 1           // 금지
val newCount = maxOf(0, deal.likeCount - 1) // 금지
if (!deal.isActive) return null             // 금지 — deal.isInactive 사용
```

### 5.6 판단 기준 요약

코드를 작성할 때, 아래 질문으로 domain에 올릴 수 있는 로직인지 판단한다:

1. **이 판단에 DB/Redis/외부 API 호출이 필요한가?**
    - NO → domain으로 올린다
    - YES → application/adapter에 유지

2. **이 판단이 2개 이상의 파일에서 반복되는가?**
    - YES → Spec 또는 DomainRule로 추출한다
    - NO → Spec 메서드로 올리되, 단일 사용이면 우선순위 낮음

3. **상수가 adapter의 companion에 숨어있는가?**
    - YES → Spec으로 이동한다. 비즈니스 상수는 domain에 있어야 한다

---

## 요약: domain 계층의 import 허용 범위

| import 대상                         | 허용 여부  |
|-----------------------------------|--------|
| `kotlin.*`, `java.*`              | 허용     |
| 같은 도메인의 `domain/*`                | 허용     |
| 다른 도메인의 `domain/*`                | 허용     |
| `application/*` (ports, usecases) | **금지** |
| `adapter/*`                       | **금지** |
| `framework/*`                     | **금지** |
| Spring 어노테이션                      | **금지** |
| Reactor (`Mono`, `Flux`)          | **금지** |
| jOOQ, R2DBC, MongoDB              | **금지** |
