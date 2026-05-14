# 로그, 에러, 보안 문서

작성일: 2026-05-14

## 1. 로그 정책

프로젝트는 세 종류의 로그를 남깁니다.

| 로그 | 담당 |
|---|---|
| 요청 로그 | `RequestLoggingInterceptor` |
| Service 호출/반환 로그 | `ServiceLoggingAspect` |
| 작업 로그 | `OperationLogService` |

## 2. 로그 파일

| 파일 | 설명 |
|---|---|
| `logs/ssafyhome.log` | 전체 로그 |
| `logs/ssafyhome-error.log` | ERROR 이상 로그 |
| IDE 콘솔 | 파일과 동일 패턴 |

공통 패턴:

```text
timestamp level pid --- [thread] [requestId] logger : message
```

## 3. RequestLoggingInterceptor

요청마다 `requestId`를 생성하고 MDC에 저장합니다.

기록 항목:

- method
- uri
- query
- remoteAddr
- sessionId
- memberId
- userAgent
- status
- durationMs
- handler

민감 query 키는 마스킹합니다.

마스킹 대상:

- `serviceKey`
- `password`
- `consumer_secret`
- `key`

## 4. ServiceLoggingAspect

Pointcut:

```java
within(com.ssafy.home..*Service)
```

Advice:

| Advice | 로그 |
|---|---|
| `@Before` | `SERVICE_CALL class method signature params` |
| `@AfterReturning` | `SERVICE_RETURN class method signature returnValue` |

주의:

- 반환값이 너무 길면 축약합니다.
- 민감 정보 패턴은 마스킹합니다.

## 5. OperationLogService

사용자/운영 작업을 메모리에 저장하고 파일 로그에도 남깁니다.

현재 저장 개수:

```text
MAX_LOGS = 300
```

대상 작업 예:

- 회원가입
- 로그인
- 로그아웃
- 관심지역 등록/삭제
- 공지 작성/수정/삭제
- 실거래 수집

## 6. 전역 예외 처리

담당:

```text
GlobalExceptionHandler
```

응답 예:

```json
{
  "success": false,
  "message": "외부 API 호출 중 오류가 발생했습니다.",
  "code": "EXTERNAL_API_ERROR",
  "status": 502,
  "path": "/api/regions/vworld/sido",
  "timestamp": "2026-05-14T11:20:28"
}
```

## 7. 예외 매핑

| 예외/상황 | HTTP | code |
|---|---:|---|
| validation fail | 400 | `VALIDATION_ERROR` |
| `SecurityException` | 401 | `UNAUTHORIZED` |
| `ExternalApiException` | 502 | `EXTERNAL_API_ERROR` |
| `IllegalArgumentException` | 400 | `BAD_REQUEST` |
| 기타 | 500 | `INTERNAL_ERROR` |

## 8. 프론트 오류 표시

`common.js`의 `parseResponse`가 다음을 감지합니다.

- HTTP status가 2xx가 아님
- JSON body의 `success === false`

오류는 화면 상단의 `.global-error` 박스로 표시됩니다.

## 9. 로그인 보안

로그인 방식:

| 항목 | 값 |
|---|---|
| 방식 | Session |
| 세션 키 | `LOGIN_MEMBER_ID` |
| 비밀번호 | BCrypt |
| 세션 timeout | 60분 |

## 10. 보호 경로

| 경로 | 처리 |
|---|---|
| `/favorites` | 미로그인 시 `/members?redirect=/favorites` |
| `/admin` | 미로그인 시 `/members?redirect=/admin` |
| `/api/favorites/**` | 미로그인 시 401 JSON |
| `/api/admin/**` | 미로그인 시 401 JSON |
| `/api/logs/**` | 미로그인 시 401 JSON |
| `/api/deals/fetch*` | 미로그인 시 401 JSON |

## 11. 현재 보안 한계

| 한계 | 설명 | 개선 방향 |
|---|---|---|
| 관리자 role 제한 없음 | 로그인만 하면 `/admin` 접근 가능 | `members.role = ADMIN` 검사 추가 |
| CSRF 보호 없음 | Spring Security filter chain 미사용 | 상태 변경 API에 CSRF token 또는 SameSite 정책 강화 |
| 작업 로그 메모리 저장 | 서버 재시작 시 사라짐 | DB 저장으로 변경 |
| API key 기본값 yml 포함 | 학습용 편의 | 운영에서는 환경변수/secret manager |

## 12. 운영 중 확인 방법

로그 tail:

```powershell
Get-Content logs\ssafyhome.log -Encoding UTF8 -Tail 100
Get-Content logs\ssafyhome-error.log -Encoding UTF8 -Tail 100
```

특정 requestId 추적:

```powershell
Select-String -Path logs\ssafyhome.log -Pattern "31367853"
```

로그인 보호 확인:

```powershell
curl.exe -s -i http://localhost:8080/api/admin/overview
```

예상: `401 LOGIN_REQUIRED`
