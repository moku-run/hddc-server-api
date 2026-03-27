# Adapter Layer Guide

adapter 계층은 헥사고날 아키텍처의 가장 바깥 레이어다. 외부 세계(HTTP, DB, 메시지 큐)와 도메인을 연결하며, **데이터 변환**만 담당한다.

## 핵심 규칙

1. **adapter에 비즈니스 로직 금지** — 변환, 매핑, 인프라 호출만
2. **Input adapter는 Input 포트만 의존** — UseCase 구현체 직접 참조 금지
3. **Output adapter는 Output 포트를 구현** — 인터페이스 계약 준수
4. **domain 모델 ↔ 인프라 타입 변환**은 adapter에서만 — Entity/Document/Request 변환

---

## 1. Input Adapter — REST Controller

### 기본 구조

```kotlin
@Tag(name = "201. 회원 가입 API", description = "회원 가입 API")
@RestController
class IndividualSignUpApi(
    private val individualSignUpUsecase: IndividualSignUpService,  // Input 포트 인터페이스만!
) {
    @Operation(
        summary = "개인 회원가입 API",
        description = """
|          설명          |
|:--------------------:|
| Phone 인증을 먼저 해야 됩니다. |
|  Email 중복은 재검증합니다.   |
|   필수 약관은 동의해야 합니다.   |
""",
    )
    @PostMapping("/public/sign-up/individual")
    fun getSignUp(
        @RequestBody request: IndividualSignUpRequest.SignUp
    ): Mono<ResponseEntity<ApiResponse<String>>> =
        individualSignUpUsecase
            .execute(request.convert)  // Request → Domain Model 변환
            .thenReturn(ApiResponse.success(payload = "회원가입에 성공했습니다."))
}
```

### 규칙

- **하나의 Controller = 하나의 Input 포트** (단일 책임)
- Controller는 Input 포트 **인터페이스**만 주입. UseCase 구현체 금지
- 반환: `Mono<ResponseEntity<ApiResponse<T>>>`
- Request → Domain Model 변환은 `request.convert` 프로퍼티로 수행
- Controller에 비즈니스 로직 금지 (if/else 분기, 조건 검사 등)

### 인증 DTO 주입

```kotlin
// 모바일 앱 사용자 인증 (companyId nullable)
@PostMapping("/app/dm/{userId}")
fun create(
    @AuthenticationPrincipal auth: MobileUserAuthenticationDTO,
    @PathVariable userId: Long
): Mono<ResponseEntity<ApiResponse<DirectMessageRoomModel>>> = ...

// 웹 백오피스 사용자 인증 (companyId non-null)
@GetMapping("/web/companies/users")
fun fetchUsers(
    @AuthenticationPrincipal auth: WebUserAuthenticationDTO,
    @PageableDefault pageable: Pageable
): Mono<ResponseEntity<ApiResponse<List<UserModel>>>> = ...

// 관리자 인증 (companyId 없음)
@PostMapping("/admin/login")
fun login(
    @RequestBody request: AdminLoginRequest
): Mono<ResponseEntity<ApiResponse<TokenModel>>> = ...
```

### 응답 래핑 패턴

```kotlin
// 200 OK (조회, 수정)
.map { ApiResponse.success(payload = it) }

// 201 Created (생성)
    .thenReturn(ApiResponse.successCreated(payload = "생성 완료"))

// 200 OK + Void (삭제, 상태 변경)
    .thenReturn(ApiResponse.successDeleted(payload = "삭제 완료"))
```

### Request DTO 패턴

```kotlin
// sealed interface로 같은 리소스의 요청을 그룹핑
interface IndividualSignUpRequest {
    data class SignUp(
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
        // @get:JsonIgnore 으로 JSON 직렬화 제외
        // convert 프로퍼티로 Domain Model 변환
        @get:JsonIgnore
        val convert
            get() = IndividualSignUpModel(
                email = email,
                name = name,
                password = password,
                passwordConfirm = passwordConfirm,
                companyName = companyName,
                department = department,
                job = job,
                phoneHead = phoneHead,
                phoneMid = phoneMid,
                phoneTail = phoneTail,
                termsList = termsList,
            )
    }
}
```

- Request 클래스 안에 `convert` 프로퍼티를 두어 **변환 로직을 Request에 캡슐화**
- Controller는 `request.convert`만 호출하면 Domain Model을 얻음

### DON'T: Controller 안티패턴

```kotlin
// 금지: Controller에 비즈니스 로직
@PostMapping("/app/dm")
fun sendMessage(@RequestBody request: SendRequest): Mono<...> {
    if (request.content.length > 1000) {  // 비즈니스 로직 금지!
        return Mono.error(ApiException(ApiResponseCode.INVALID_CONTENT))
    }
    ...
}

// 금지: UseCase 구현체 직접 주입
class MyApi(
    private val signUpUsecase: IndividualSignUpService,  // 구현체 금지! Input 포트를 써야 함
)

// 금지: Controller에서 여러 UseCase 호출
class MyApi(
    private val signUpInput: IndividualSignUpService,
    private val loginInput: LoginInput,  // 분리해야 함!
)
```

---

## 2. Output Adapter — Command (쓰기)

### 기본 구조

```kotlin
@Component
class UserCommandAdapter(
    private val repository: MstUserRepository,
    private val passwordEncoder: PasswordEncoder,
) : UserCommandPort {

    // 생성: model → entity 변환 → 저장 → ID 반환
    override fun save(model: CreateUserModel): Mono<Long> =
        repository
            .save(model.convert(passwordEncoder))
            .map { it.id!! }

    override fun save(model: IndividualSignUpModel): Mono<Long> =
        repository
            .save(model.convert(passwordEncoder))
            .map { it.id!! }

    // 수정: ID로 조회 → entity 메서드 호출 → 저장
    override fun update(userId: Long, model: UpdateUserModel): Mono<Void> =
        repository
            .loadById(userId)
            .flatMap {
                it.update(model, passwordEncoder)
                repository.save(it)
            }
            .then()

    override fun update(userId: Long, model: UpdateMobileUserProfileModel): Mono<Void> =
        repository
            .loadById(userId)
            .flatMap {
                it.update(model)
                repository.save(it)
            }
            .then()

    // 역할 위임: 두 엔티티를 동시에 조회하여 역할 교환
    override fun delegateAdmin(adminId: Long, targetId: Long): Mono<Void> =
        Mono.zip(
            repository.loadById(adminId),
            repository.loadById(targetId),
        ).flatMap { tuple ->
            val admin = tuple.t1
            val target = tuple.t2
            admin.demoteToMember()
            target.promoteToAdmin()
            repository.save(admin)
                .then(repository.save(target))
        }.then()

    // 삭제: Flux로 순회하며 soft delete
    override fun delete(userIdList: List<Long>): Mono<Void> =
        Flux
            .fromIterable(userIdList)
            .flatMap {
                repository
                    .loadById(it)
                    .flatMap { found ->
                        found.delete()
                        repository.save(found)
                    }
            }
            .then()

    // 회사 탈퇴
    override fun removeMembersFromCompany(userIdList: List<Long>): Mono<Void> =
        Flux
            .fromIterable(userIdList)
            .flatMap { userId ->
                repository
                    .loadById(userId)
                    .flatMap {
                        it.leaveCompany()
                        repository.save(it)
                    }
            }
            .then()
}
```

### 규칙

- Output 포트 인터페이스를 구현한다
- Domain Model ↔ Entity 변환은 Entity의 companion object에서 수행
- Entity의 도메인 메서드 (`update()`, `delete()`, `joinCompany()`)를 호출하여 상태 변경
- adapter 자체에 비즈니스 로직 금지

### Repository 확장 함수: loadById 패턴

```kotlin
interface MstUserRepository : R2dbcRepository<MstUserEntity, Long> {
    fun findByIdAndIsDeletedFalse(id: Long): Mono<MstUserEntity>
    fun findByEmailAndIsDeletedFalse(email: String): Mono<MstUserEntity>
    fun findAllByIdInAndIsDeletedFalse(idList: List<Long>): Flux<MstUserEntity>
    fun findAllByCompanyIdAndIsDeletedFalse(companyId: Long): Flux<MstUserEntity>
    fun findByCompanyIdAndRoleAndIsDeletedFalse(companyId: Long, role: UserRole): Mono<MstUserEntity>
    fun findByPhoneFullAndIsDeletedFalse(phone: String): Mono<MstUserEntity>
    fun findByNameAndPhoneFullAndIsDeletedFalse(name: String, phoneFull: String): Mono<MstUserEntity>
    fun findByEmailAndPhoneFullAndIsDeletedFalse(email: String, phoneFull: String): Mono<MstUserEntity>
    fun findByNameAndPhoneFullAndRoleInAndIsDeletedFalse(
        name: String,
        phoneFull: String,
        role: Collection<UserRole>
    ): Mono<MstUserEntity>
    fun findByEmailAndPhoneFullAndRoleInAndIsDeletedFalse(
        email: String,
        phoneFull: String,
        role: Collection<UserRole>
    ): Mono<MstUserEntity>
    fun findByPhoneFullAndRoleInAndIsDeletedFalse(phoneFull: String, role: Collection<UserRole>): Mono<MstUserEntity>
}

// 확장 함수로 "not-found → 에러" 변환을 재사용 가능하게 분리
fun MstUserRepository.loadById(id: Long): Mono<MstUserEntity> =
    findByIdAndIsDeletedFalse(id)
        .switchIfEmpty(Mono.error(ApiException(ApiResponseCode.USER_NOT_FOUND)))

fun MstUserRepository.loadAllById(idList: List<Long>): Mono<List<MstUserEntity>> =
    findAllByIdInAndIsDeletedFalse(idList)
        .collectList()
        .flatMap { users ->
            if (users.size != idList.distinct().size) {
                Mono.error(ApiException(ApiResponseCode.USER_NOT_FOUND))
            } else {
                Mono.just(users)
            }
        }

fun MstUserRepository.loadOwnerByCompanyId(companyId: Long): Mono<MstUserEntity> =
    findByCompanyIdAndRoleAndIsDeletedFalse(companyId, UserRole.ADMIN)
        .switchIfEmpty(
            Mono.error(ApiException(ApiResponseCode.COMPANY_NOT_FOUND_ADMIN))
        )
```

- `find*` — `Mono.empty()` 반환 가능 (호출자가 처리)
- `load*` — not-found 시 바로 `ApiException` 발생 (fail-fast)

---

## 3. Output Adapter — Guard (인가)

```kotlin
@Service
class DirectMessageGuardAdapter(
    private val dsl: DSLContext,
) : DirectMessageGuard {

    override fun requireMemberOfMessageRoom(
        auth: MobileUserAuthenticationDTO,
        roomId: MessageRoomId,
    ): Mono<Void> =
        Mono.from(
            dsl.selectCount()
                .from(JMST_DM_MEMBER)
                .where(
                    JMST_DM_MEMBER.USER_ID.eq(auth.id)
                        .and(JMST_DM_MEMBER.ROOM_ID.eq(roomId.value))
                        .and(JMST_DM_MEMBER.IS_DELETED.isFalse)
                )
        )
            .map { it.value1() }
            .flatMap { count ->
                throwUnless(count > 0, ApiResponseCode.DM_NOT_MEMBER)
            }
}
```

- jOOQ `DSLContext`로 복합 JOIN/조건 쿼리 수행
- `throwIf`/`throwUnless`로 `Mono<Void>` 반환

---

## 4. Output Adapter — Validator (검증)

```kotlin
@Service
class UserValidationAdapter(
    private val repository: MstUserRepository,
) : UserValidator {

    // 단건: loadById로 존재 확인 (없으면 자동으로 에러)
    override fun validate(userId: Long): Mono<Void> =
        repository
            .loadById(userId)
            .then()

    // 다건: 카운트 비교
    override fun validate(userIdList: List<Long>): Mono<Void> =
        repository
            .findAllByIdInAndIsDeletedFalse(userIdList)
            .count()
            .flatMap {
                throwUnless(
                    it == userIdList.size.toLong(),
                    ApiResponseCode.USER_INVALID
                )
            }
            .then()
}
```

---

## 5. Entity 패턴

### 기본 구조

```kotlin
@Table("mst_user")
class MstUserEntity(
    @Id var id: Long? = null,
    var email: String,
    var name: String,
    var password: String,
    var phoneFull: String,
    var companyId: Long?,
    var role: UserRole,
) : BaseEntity() {

    // 도메인 메서드: 상태 변경 로직
    fun update(model: UpdateUserModel, encoder: PasswordEncoder) {
        this.name = model.name
        this.password = encoder.encode(model.password)
    }

    fun joinCompany(companyId: Long, role: UserRole = UserRole.USER) {
        this.companyId = companyId
        this.role = role
    }

    fun leaveCompany() {
        this.companyId = null
        this.role = UserRole.GUEST
    }

    fun promoteToAdmin() {
        this.role = UserRole.ADMIN
    }
    fun demoteToMember() {
        this.role = UserRole.USER
    }

    override fun delete() {
        super.delete()
        this.remark3 = this.email  // 삭제 전 이메일 보관
        this.email = UUID.randomUUID().toString()
        this.password = UUID.randomUUID().toString()
    }

    // companion object: Domain Model → Entity 변환
    companion object {
        fun CreateUserModel.convert(encoder: PasswordEncoder) = MstUserEntity(
            email = email,
            name = name,
            password = encoder.encode(password),
            phoneFull = "$phoneHead$phoneMid$phoneTail",
            companyId = companyId,
            role = UserRole.USER,
        )

        fun IndividualSignUpModel.convert(encoder: PasswordEncoder) = MstUserEntity(
            email = email,
            name = name,
            password = encoder.encode(password),
            phoneFull = "$phoneHead$phoneMid$phoneTail",
            companyId = null,
            role = role,
        )
    }
}
```

### 규칙

- `BaseEntity`를 상속: `createdAt`, `updatedAt`, `isDeleted`, `deletedAt` 자동 관리
- **상태 변경 메서드**를 Entity에 정의 (`update()`, `delete()`, `joinCompany()`)
- Domain Model → Entity 변환은 **companion object 확장 함수**로 정의
- Entity → Domain Model 변환이 필요하면 Entity에 `toDomain()` 메서드 정의
- Entity 테이블명: `Mst_` (마스터), `His_` (이력)

### DON'T: Entity 안티패턴

```kotlin
// 금지: Entity에서 다른 Entity 참조 (JPA 관계 매핑 패턴)
class MstUserEntity(
    @ManyToOne val company: MstCompanyEntity,  // R2DBC는 관계 매핑 없음!
)

// 금지: Entity에 비즈니스 판단 로직
class MstUserEntity(...) {
    fun canSendMessage(): Boolean = role != UserRole.GUEST  // UseCase/Guard의 역할!
}
```

---

## 6. MongoDB Document 패턴

```kotlin
@Document(collection = "dm_messages")
data class DirectMessageDocument(
    @Id val id: String? = null,
    @Field("room_id") val roomId: String,
    @Field("sender_id") val senderId: Long,
    @Field("content") val content: String,
    @Field("content_type") val contentType: String,
) {
    // Document → Domain Value Object 변환
    val directMessageId get() = MessageId(id!!)
}
```

- `@Document`, `@Field` 어노테이션은 adapter 레이어에서만
- Domain Model과의 변환은 확장 함수 또는 computed property로 수행

---

## 7. Kafka Listener 패턴

```kotlin
@Component
class DirectMessageEventListener(
    private val broadcastHandler: DmBroadcastHandler,
    private val notificationHandler: DmNotificationHandler,
    private val readHandler: DmReadHandler,
) {
    @KafkaListener(
        topics = [KafkaTopic.DM_MESSAGES_TOPIC_VALUE],
        groupId = "DM_BROADCAST",
        containerFactory = LATEST_KAFKA_LISTENER_CONTAINER_FACTORY,
    )
    fun broadcast(record: ConsumerRecord<String, String>, ack: Acknowledgment) {
        try {
            broadcastHandler.handle(record).block()  // Kafka listener에서는 .block() 허용
        } catch (e: Exception) {
            logWarn("DM broadcast error", e)
        } finally {
            ack.acknowledge()  // 항상 ACK (consumer 멈춤 방지)
        }
    }
}
```

- Kafka listener는 `.block()` 사용이 허용되는 **유일한 예외**
- 3개 consumer group: NOTIFICATION, BROADCAST, READ
- 에러 시 로그만 남기고 ACK (consumer 그룹 멈춤 방지)
- 실제 로직은 Handler 클래스에 위임

---

## 데이터 변환 요약

```
HTTP Request
    ↓  request.convert (Request DTO의 computed property)
Domain Model
    ↓  model.convert(encoder) (Entity companion 확장 함수)
R2DBC Entity / MongoDB Document
    ↓  entity.toDomain() 또는 record → Model 매핑
Domain Model
    ↓  ApiResponse.success(payload = model)
HTTP Response
```

각 변환 지점은 명확한 레이어에 위치한다:

- **Request → Domain Model**: Request DTO의 `convert` 프로퍼티 (adapter/input)
- **Domain Model → Entity**: Entity companion의 확장 함수 (adapter/out)
- **Entity → Domain Model**: Entity의 `toDomain()` 또는 Query adapter의 매핑 (adapter/out)
- **Domain Model → Response**: Controller에서 `ApiResponse.success()` 래핑 (adapter/input)
