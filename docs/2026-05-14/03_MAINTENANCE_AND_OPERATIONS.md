# 유지보수 및 운영 문서

작성일: 2026-05-14

## 1. 운영 개요

SSAFY Home은 로컬 또는 교육용 서버에서 MySQL과 함께 실행되는 Spring Boot WAR 애플리케이션입니다.  
운영에서 가장 중요한 것은 DB 연결, 외부 API 키, 로그 확인, 관리자 대시보드 접근입니다.

## 2. 빌드

```powershell
.\mvnw.cmd -q clean package
```

빌드 결과:

```text
target/ssafyhome-0.0.1-SNAPSHOT.war
```

빌드 실패 시 우선 확인:

| 증상 | 확인 |
|---|---|
| DB 연결 실패 | MySQL 실행, 계정 `ssafy/ssafy`, DB 권한 |
| Mapper 오류 | XML 문법, Mapper interface 메서드명 |
| JSP 관련 오류 | `tomcat-embed-jasper`, view prefix/suffix |
| Lombok 오류 | IDE annotation processing, Maven dependency |

## 3. 실행

```powershell
java -Dfile.encoding=UTF-8 -jar target\ssafyhome-0.0.1-SNAPSHOT.war
```

백그라운드 실행 예:

```powershell
$out = Join-Path (Get-Location) 'target\app-run.out.log'
$err = Join-Path (Get-Location) 'target\app-run.err.log'
Start-Process -FilePath java -ArgumentList @('-Dfile.encoding=UTF-8','-jar','target\ssafyhome-0.0.1-SNAPSHOT.war') -RedirectStandardOutput $out -RedirectStandardError $err -WindowStyle Hidden
```

## 4. 서버 중지

```powershell
Get-CimInstance Win32_Process |
  Where-Object { $_.Name -eq 'java.exe' -and $_.CommandLine -like '*ssafyhome-0.0.1-SNAPSHOT.war*' } |
  ForEach-Object { Stop-Process -Id $_.ProcessId -Force }
```

## 5. 접속 확인

```powershell
curl.exe -s -o NUL -w "%{http_code}" http://localhost:8080/
```

정상: `200`

## 6. 로그 위치

| 파일 | 설명 |
|---|---|
| `logs/ssafyhome.log` | 전체 애플리케이션 로그 |
| `logs/ssafyhome-error.log` | ERROR 이상 로그 |
| `target/app-run.out.log` | 백그라운드 실행 시 표준 출력 |
| `target/app-run.err.log` | 백그라운드 실행 시 표준 에러 |

## 7. 로그 키워드

| 키워드 | 의미 |
|---|---|
| `REQUEST_START` | 요청 시작 |
| `REQUEST_END` | 요청 종료 |
| `REQUEST_ERROR` | Interceptor 관점 예외 |
| `AUTH_REQUIRED` | 로그인 필요 |
| `AUTH_PASS` | 로그인 보호 통과 |
| `SERVICE_CALL` | Service 메서드 호출 |
| `SERVICE_RETURN` | Service 메서드 반환 |
| `OPERATION_LOG` | 작업 로그 기록 |
| `EXTERNAL_API_ERROR` | 외부 API 실패 |

## 8. 관리자 대시보드 운영

접속:

```text
http://localhost:8080/admin
```

조건:

- 로그인 필요
- 미로그인 시 `/members?redirect=/admin`으로 이동

관리자 화면에서 확인할 것:

| 영역 | 확인 |
|---|---|
| 요약 지표 | 회원 수, 실거래 수, 단지 수, 관심지역/공지 수 |
| 거래 유형별 현황 | `property_deals` 적재 균형 |
| 최근 작업 로그 | 수집/회원/공지/관심지역 작업 |
| 현재 DB 조회 | 테이블별 데이터 상태 |
| 최근 수집 실거래 | 마지막 수집 데이터 확인 |

## 9. DB 관리

기본 연결:

| 항목 | 값 |
|---|---|
| DB | `ssafy_home` |
| username | `ssafy` |
| password | `ssafy` |
| URL | `jdbc:mysql://localhost:3306/ssafy_home?...` |

`schema.sql`은 `create table if not exists` 구조입니다.  
서버 시작 시 기존 데이터는 유지하고 없는 테이블만 생성합니다.

## 10. 외부 API 관리

| API | 설정 |
|---|---|
| data.go.kr | `app.public-data.service-key`, env `PUBLIC_DATA_SERVICE_KEY` |
| VWorld | `app.vworld.key`, env `VWORLD_KEY` |
| SGIS | `app.sgis.consumer-key`, `app.sgis.consumer-secret` |

주의:

- data.go.kr key는 URL 인코딩된 인증키를 사용합니다.
- VWorld는 현재 코드에 통합 키가 들어 있지만 실제 API가 `INCORRECT_KEY`를 반환할 수 있습니다.
- API 키는 운영에서는 환경변수로 분리하는 것을 권장합니다.

## 11. 장애 대응

### 11.1 애플리케이션 시작 실패

확인 순서:

1. `logs/ssafyhome-error.log`
2. `target/app-run.err.log`
3. MySQL 실행 여부
4. DB 계정/비밀번호
5. 포트 8080 점유 여부

포트 확인:

```powershell
Get-NetTCPConnection -LocalPort 8080
```

### 11.2 로그인 필요 오류

API 응답:

```json
{"success":false,"message":"로그인이 필요한 기능입니다.","code":"LOGIN_REQUIRED","status":401}
```

조치:

- `/members`에서 로그인
- 세션 만료 여부 확인
- 보호 경로가 의도한 경로인지 `WebConfig` 확인

### 11.3 외부 API 오류

대표 응답:

```json
{
  "success": false,
  "code": "EXTERNAL_API_ERROR",
  "status": 502
}
```

조치:

- API 키 확인
- endpoint 확인
- 요청 파라미터 확인
- 원본 응답은 에러 로그에서 확인

## 12. 유지보수 원칙

| 원칙 | 설명 |
|---|---|
| Controller는 얇게 | 요청/응답과 세션만 처리 |
| Service에 업무 로직 | 검증, 외부 API, 트랜잭션 후보 로직 |
| SQL은 Mapper XML | 복잡한 조회는 XML에 명시 |
| 공통 로직은 공통 계층 | 로그, 예외, 인증은 Interceptor/AOP/Advice |
| 화면 로직은 JS | JSP에는 구조 중심, 동작은 JS |

## 13. 주기 점검 항목

| 주기 | 항목 |
|---|---|
| 매 실행 | `/`, `/api/deals/summary`, `/admin` 접근 확인 |
| 매 수집 후 | `property_deals` 건수, `OPERATION_LOG` 확인 |
| 외부 API 오류 시 | API key, quota, endpoint 확인 |
| DB 변경 시 | ERD, 컬럼정의서, AdminController 테이블 설정 업데이트 |
| UI 변경 시 | desktop/mobile 화면 확인 |
