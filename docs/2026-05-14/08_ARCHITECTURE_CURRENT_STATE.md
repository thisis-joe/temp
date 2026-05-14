# 현재 아키텍처 문서

작성일: 2026-05-14

## 1. 아키텍처 요약

SSAFY Home은 JSP 기반 Spring MVC 웹 애플리케이션입니다.

```mermaid
flowchart LR
    Browser["Browser"] --> JSP["JSP View"]
    JSP --> JS["Static JavaScript"]
    JS --> Controller["REST Controller"]
    Controller --> Service["Service"]
    Service --> Mapper["MyBatis Mapper"]
    Mapper --> DB["MySQL"]
    Service --> External["External APIs"]

    Controller --> Exception["GlobalExceptionHandler"]
    Service --> AOP["ServiceLoggingAspect"]
    Browser --> Interceptor["Request/Login Interceptor"]
    AOP --> Logs["Console/File Logs"]
    Interceptor --> Logs
    Exception --> Logs
```

## 2. 계층 구조

| 계층 | 구현 |
|---|---|
| View | JSP |
| Client Logic | Vanilla JS |
| API | Spring MVC REST Controller |
| Business | Service |
| Persistence | MyBatis Mapper XML |
| DB | MySQL |
| Auth | Session + Interceptor |
| Logging | Interceptor + AOP + Logback |
| Error | ControllerAdvice |

## 3. 요청 흐름

### 페이지 요청

```mermaid
sequenceDiagram
    participant B as Browser
    participant I as RequestLoggingInterceptor
    participant L as LoginCheckInterceptor
    participant P as PageController
    participant J as JSP

    B->>I: GET /houses
    I->>L: preHandle
    L->>P: pass
    P-->>J: view name houses
    J-->>B: HTML
    I-->>B: REQUEST_END log
```

### API 요청

```mermaid
sequenceDiagram
    participant JS as JavaScript
    participant I as Interceptor
    participant C as Controller
    participant A as AOP
    participant S as Service
    participant M as Mapper
    participant DB as MySQL

    JS->>I: fetch /api/houses
    I->>C: request
    C->>A: call service
    A->>S: SERVICE_CALL
    S->>M: query
    M->>DB: SQL
    DB-->>M: rows
    M-->>S: DTO
    S-->>A: return
    A-->>C: SERVICE_RETURN
    C-->>JS: ApiResponse
```

## 4. 도메인 흐름

| 도메인 | 흐름 |
|---|---|
| 회원 | JSP → `members.js` → `AuthController`/`MemberController` → `MemberService` → `MemberMapper` |
| 실거래 | JSP → `deals.js` → `DealController` → `DealService` → data.go.kr/MyBatis |
| 단지 | JSP → `houses.js` → `HouseController` → `HouseService` → `HouseMapper` |
| 관심지역 | JSP → `favorites.js` → `FavoriteController` → `FavoriteService` → `FavoriteMapper` |
| 공지 | JSP → `notices.js` → `NoticeController` → `NoticeService` → `NoticeMapper` |
| 지역 | JSP → `regions.js` → `RegionController` → `RegionService` → DB/VWorld/SGIS |
| 관리자 | JSP → `admin.js` → `AdminController`/`OperationLogController` → DB/OperationLogService |

## 5. 데이터 관계

```mermaid
erDiagram
    MEMBERS ||--o{ FAVORITES : member_id
    MEMBERS ||--o{ NOTICES : writer_id
    HOUSEINFOS ||--o{ HOUSEDEALS : apt_seq

    MEMBERS {
        BIGINT id PK
        VARCHAR email
        VARCHAR password
    }
    FAVORITES {
        BIGINT id PK
        BIGINT member_id FK
        VARCHAR lawd_cd
    }
    NOTICES {
        BIGINT id PK
        BIGINT writer_id FK
    }
    HOUSEINFOS {
        VARCHAR apt_seq PK
    }
    HOUSEDEALS {
        INT no PK
        VARCHAR apt_seq FK
    }
```

## 6. 공통 처리

| 공통 처리 | 담당 | 설명 |
|---|---|---|
| 요청 로그 | `RequestLoggingInterceptor` | requestId, status, duration |
| 로그인 보호 | `LoginCheckInterceptor` | 세션 검사 |
| Service 로그 | `ServiceLoggingAspect` | 호출 파라미터, 반환값 |
| 예외 처리 | `GlobalExceptionHandler` | 오류 JSON 통일 |
| 작업 로그 | `OperationLogService` | 사용자 작업 로그 |

## 7. 관리자 구조

관리자는 일반 사용 흐름과 분리됩니다.

```mermaid
flowchart TB
    Footer["Footer 작은 관리자 링크"] --> AdminPage["/admin"]
    AdminPage --> AdminJS["admin.js"]
    AdminJS --> Overview["/api/admin/overview"]
    AdminJS --> Tables["/api/admin/tables"]
    AdminJS --> Rows["/api/admin/rows"]
    AdminJS --> Logs["/api/logs"]
    Overview --> DB["MySQL"]
    Rows --> DB
    Logs --> Memory["OperationLogService memory"]
```

## 8. 현재 설계상 주의

| 항목 | 설명 |
|---|---|
| 관리자 권한 | 현재는 로그인 여부만 확인. role 기반 ADMIN 제한은 추가 가능 |
| 작업 로그 | 메모리 기반이라 서버 재시작 시 초기화 |
| VWorld | 키 권한 문제 시 502 오류 |
| 대용량 테이블 | `housedeals`는 관리자 overview에서 추정 count 사용 |
| JSP | WAR 패키징과 Jasper 의존성 필요 |
