# Backend Code Guide

이 가이드는 백엔드 코드 작성 시 따라야 할 헥사고날 아키텍처 원칙을 정의한다.
프로젝트에 독립적이며, 동일한 아키텍처를 사용하는 모든 프로젝트에 적용 가능하다.

## 핵심 원칙

1. **의존성 방향은 안쪽으로만 흐른다** — adapter → application → domain. 역방향 의존 금지
2. **domain은 순수하다** — Spring 어노테이션, 인프라 라이브러리 import 금지
   - **예외:** application 계층에서 `Pageable`, `Page` 등 Spring Data 페이징 타입은 허용
3. **비즈니스 로직은 domain에** — use case는 오케스트레이션만, adapter는 변환만
4. **인터페이스로 계층을 분리한다** — use case는 Input 포트, 인프라는 Output 포트
5. **Spring Boot + VirtualThread 모델** — 동기 스타일, `@Transactional`로 트랜잭션 관리

## 가이드 파일 참조

| 작업 대상                                  | 가이드 파일                                                 |
|----------------------------------------|--------------------------------------------------------|
| **전체 아키텍처 규칙**                         | [hexagonal-architecture.md](hexagonal-architecture.md) |
| **Domain 계층**                          | [domain-layer.md](domain-layer.md)                     |
| **Application 계층** (Port, UseCase)     | [application-layer.md](application-layer.md)           |
| **Adapter 계층** (Controller, DB, Kafka) | [adapter-layer.md](adapter-layer.md)                   |

## 빠른 체크리스트

코드 작성 전 확인:

- [ ] domain 모델에 Spring/인프라 어노테이션이 없는가?
- [ ] use case가 adapter를 직접 참조하지 않는가? (port 인터페이스만 사용)
- [ ] 다른 도메인은 Input 포트 인터페이스를 통해서만 호출하는가?
- [ ] 반환 타입이 적절한가? (Spring Boot + VirtualThread 모델: 동기 반환)
- [ ] Guard(인가)와 Validator(검증)를 올바르게 구분했는가?
- [ ] 트랜잭션 범위가 write 연산에만 한정되어 있는가?
