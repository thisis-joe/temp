# SSAFY Home Detailed Architecture

이 문서는 SSAFY Home 프로젝트의 상세 아키텍처를 글로 설명합니다.  
동일한 내용을 Mermaid 다이어그램 중심으로 보고 싶다면 `docs/ARCHITECTURE_DETAILED.mmd`를 확인합니다.

## 1. 시스템 목적

SSAFY Home은 부동산 실거래와 아파트 단지 정보를 제공하는 Spring Boot MVC 웹 애플리케이션입니다.

서비스는 세 가지 데이터 축을 사용합니다.

| 축 | 설명 |
|---|---|
| 기존 DB 데이터 | `dongcodes`, `houseinfos`, `housedeals` 기반 단지/거래 조회 |
| 외부 API 데이터 | data.go.kr 실거래 API를 호출해 `property_deals`에 저장 |
| 사용자/운영 데이터 | `members`, `favorites`, `notices`, 메모리 작업 로그 |

## 2. 실행 구조

프로젝트는 WAR로 패키징되며 내장 Tomcat으로 실행됩니다.  
JSP는 `tomcat-embed-jasper`가 처리하고, ViewResolver는 `application.yml`의 prefix/suffix 설정으로 JSP를 찾습니다.

```text
Controller return "home"
  -> /WEB-INF/views/home.jsp
```

정적 리소스는 Spring Boot 기본 정적 경로인 `src/main/resources/static`에서 제공됩니다.

## 3. 요청 처리 흐름

### 3.1 일반 페이지 요청

1. 사용자가 `/houses` 같은 페이지 URL을 요청합니다.
2. `RequestLoggingInterceptor`가 requestId를 만들고 요청 시작 로그를 남깁니다.
3. `LoginCheckInterceptor`가 보호 경로인지 확인합니다.
4. `PageController`가 JSP view name을 반환합니다.
5. JSP가 HTML을 렌더링하고 CSS/JS를 로드합니다.
6. `RequestLoggingInterceptor`가 응답 status와 duration을 기록합니다.

### 3.2 API 요청

1. JSP에서 로드된 JS가 `fetch()`로 `/api/**`를 호출합니다.
2. 요청 로깅 Interceptor가 requestId를 MDC에 저장합니다.
3. 로그인 보호 대상이면 `LoginCheckInterceptor`가 세션을 검사합니다.
4. REST Controller가 DTO와 파라미터를 받습니다.
5. Controller는 Service를 호출합니다.
6. `ServiceLoggingAspect`가 `@Before`로 메서드 Signature와 파라미터를 기록합니다.
7. Service는 Mapper 또는 외부 API를 호출합니다.
8. `ServiceLoggingAspect`가 `@AfterReturning`으로 반환값을 기록합니다.
9. Controller는 `ApiResponse`로 응답합니다.
10. JS는 응답을 화면에 렌더링합니다.

## 4. 계층별 상세

### 4.1 View Layer

| 파일 | 역할 |
|---|---|
| `home.jsp` | 서비스 홈, 주요 기능 진입 |
| `deals.jsp` | 실거래 수집/검색/요약 |
| `houses.jsp` | 아파트 단지와 거래 이력 검색 |
| `members.jsp` | 회원가입/로그인/회원 관리 |
| `favorites.jsp` | 회원별 관심지역 |
| `notices.jsp` | 공지사항 |
| `regions.jsp` | DB/VWorld/SGIS 지역정보 확인 |
| `admin.jsp` | 운영자 대시보드 |

상단 네비게이션에서는 관리자 링크를 숨기고, footer 하단 오른쪽의 작은 링크로 관리자 페이지에 접근합니다.

### 4.2 JavaScript Layer

| 파일 | 역할 |
|---|---|
| `common.js` | API 공통 처리, 전역 오류 표시, 다크모드 쿠키, 세션 표시, footer 생성 |
| `deals.js` | 실거래 API 수집/검색/요약 화면 제어 |
| `houses.js` | 지역 검색, 단지 검색, 상세 거래 이력 렌더링 |
| `members.js` | 회원가입/로그인/로그아웃/회원 조회 |
| `favorites.js` | 관심지역 등록/조회/삭제 |
| `notices.js` | 공지사항 CRUD |
| `regions.js` | 지역 코드/VWorld/SGIS 응답 표시 |
| `admin.js` | 관리자 지표, 로그, DB 테이블 조회 |

JS는 서버 응답의 `success:false` 또는 HTTP error를 감지하면 브라우저 상단에 오류 박스를 표시합니다.

### 4.3 Controller Layer

Controller는 요청을 받아 Service에 위임하고 응답 형태를 정리합니다.

| Controller | 주요 역할 |
|---|---|
| `PageController` | JSP 화면 라우팅 |
| `AuthController` | 회원가입, 로그인, 로그아웃, 현재 사용자 |
| `MemberController` | 회원 목록/상세/수정/삭제 |
| `DealController` | 실거래 수집, 통합 수집, 검색, 요약 |
| `HouseController` | 법정동 검색, 단지 검색, 단지 상세 |
| `FavoriteController` | 관심지역 목록/등록/삭제 |
| `NoticeController` | 공지사항 CRUD |
| `RegionController` | 지역 코드와 외부 지역 API |
| `AdminController` | 운영 대시보드, DB 테이블 조회 |
| `OperationLogController` | 작업 로그 조회 |

### 4.4 Service Layer

Service는 실제 업무 규칙을 담당합니다.

| Service | 주요 책임 |
|---|---|
| `MemberService` | BCrypt 암호화, 로그인 검증, 회원 CRUD |
| `DealService` | 외부 API 호출, XML 파싱, 실거래 저장/검색/요약 |
| `HouseService` | 단지/지역 검색과 상세 거래 조회 |
| `FavoriteService` | 회원별 관심지역 소유권 기준 처리 |
| `NoticeService` | 공지 CRUD와 조회수 증가 |
| `RegionService` | DB 지역 조회, VWorld/SGIS API 호출 |
| `OperationLogService` | 작업 로그 메모리 저장과 파일 로그 출력 |

모든 Service 호출은 AOP 로그 대상입니다.

### 4.5 Persistence Layer

MyBatis Mapper는 Java 인터페이스와 XML SQL로 구성됩니다.

| Mapper XML | 주요 SQL |
|---|---|
| `MemberMapper.xml` | 회원 insert/select/update/delete |
| `DealMapper.xml` | 실거래 insert/delete/search/summarize |
| `HouseMapper.xml` | 지역 검색, 단지 검색, 상세, 거래 이력 |
| `FavoriteMapper.xml` | 관심지역 insert/select/delete |
| `NoticeMapper.xml` | 공지 insert/select/update/delete/view count |
| `RegionMapper.xml` | 시도/시군구/동 코드 조회 |

## 5. 데이터 아키텍처

### 5.1 핵심 테이블

| 테이블 | 역할 | 주요 관계 |
|---|---|---|
| `members` | 회원 | `favorites.member_id`, `notices.writer_id`의 부모 |
| `favorites` | 관심지역 | `members.id` 참조, 회원 삭제 시 cascade |
| `notices` | 공지사항 | `members.id` 참조, 회원 삭제 시 writer_id null |
| `dongcodes` | 법정동 코드 | 단지 검색 조인과 지역 선택 기준 |
| `houseinfos` | 단지 기본정보 | `housedeals.apt_seq`의 부모 |
| `housedeals` | 단지별 거래 | `houseinfos.apt_seq` 참조 |
| `property_deals` | 외부 API 수집 실거래 | 지역/월/유형 인덱스 기반 조회 |

### 5.2 데이터 흐름

기존 DB 조회:

```text
houses.jsp -> houses.js -> HouseController -> HouseService -> HouseMapper -> dongcodes/houseinfos/housedeals
```

외부 실거래 수집:

```text
deals.jsp -> deals.js -> DealController -> DealService -> data.go.kr -> DealMapper -> property_deals
```

관리자 DB 조회:

```text
admin.jsp -> admin.js -> AdminController -> JdbcTemplate -> 허용된 테이블
```

## 6. 인증 아키텍처

세션 기반 인증을 사용합니다.

| 항목 | 설명 |
|---|---|
| 세션 키 | `AuthController.LOGIN_MEMBER_ID` |
| 저장 위치 | `HttpSession` |
| 로그인 처리 | `AuthController.login()` |
| 로그아웃 처리 | `session.invalidate()` |
| 권한 검사 | `LoginCheckInterceptor` |

보호 대상:

| 경로 | 이유 |
|---|---|
| `/favorites` | 개인 관심지역 화면 |
| `/admin` | 운영자 대시보드 |
| `/api/favorites/**` | 개인 관심지역 API |
| `/api/admin/**` | 운영 DB 조회 API |
| `/api/logs/**` | 운영 로그 API |
| `/api/deals/fetch`, `/api/deals/fetch-all` | 데이터 적재 API |

API는 미로그인 시 401 JSON을 반환하고, 페이지는 `/members?redirect=...`로 이동합니다.

## 7. 로그 아키텍처

### 7.1 요청 로그

`RequestLoggingInterceptor`가 요청마다 다음 값을 기록합니다.

| 값 | 목적 |
|---|---|
| requestId | 같은 요청의 로그 연결 |
| method, uri, query | 요청 식별 |
| remoteAddr, userAgent | 접속 맥락 |
| sessionId, memberId | 로그인 사용자 맥락 |
| status, durationMs | 결과와 성능 |
| handler | 처리 Controller 메서드 |

### 7.2 Service AOP 로그

`ServiceLoggingAspect`는 Service 전체에 적용됩니다.

| Advice | 내용 |
|---|---|
| `@Before` | 클래스명, 메서드명, Signature, 파라미터 |
| `@AfterReturning` | 클래스명, 메서드명, Signature, 반환값 |

### 7.3 파일 로그

| 파일 | 내용 |
|---|---|
| `logs/ssafyhome.log` | 전체 로그 |
| `logs/ssafyhome-error.log` | ERROR 이상 |
| IDE 콘솔 | 파일과 같은 패턴 |

## 8. 예외 아키텍처

`GlobalExceptionHandler`가 예외를 API 응답으로 통일합니다.

| 상황 | HTTP status | 코드 |
|---|---:|---|
| 요청값 검증 실패 | 400 | `VALIDATION_ERROR` |
| 로그인 필요 | 401 | `UNAUTHORIZED` |
| 외부 API 실패 | 502 | `EXTERNAL_API_ERROR` |
| 기타 오류 | 500 | `INTERNAL_ERROR` |

프론트 공통 JS는 이 응답을 받아 브라우저에 오류 메시지를 표시합니다.

## 9. 관리자 아키텍처

관리자 페이지는 일반 사용자 흐름과 분리됩니다.

| 영역 | 데이터 출처 |
|---|---|
| 요약 metric | `AdminController.overview()` |
| 거래 유형별 현황 | `property_deals group by deal_type` |
| 최근 작업 로그 | `OperationLogService.find()` |
| DB 테이블 조회 | `AdminController.rows()` |
| 최근 수집 실거래 | `property_deals order by id desc limit 8` |

관리자 DB 조회는 임의 SQL을 받지 않고 서버에 정의된 허용 테이블만 조회합니다.

## 10. 배포/실행 관점

빌드:

```powershell
.\mvnw.cmd -q clean package
```

실행:

```powershell
java -Dfile.encoding=UTF-8 -jar target\ssafyhome-0.0.1-SNAPSHOT.war
```

검증:

```powershell
curl.exe -s -o NUL -w "%{http_code}" http://localhost:8080/
curl.exe -s "http://localhost:8080/api/deals/summary?lawdCd=11110&dealYmd=202407"
```

## 11. 변경 영향 요약

| 변경 대상 | 함께 확인할 곳 |
|---|---|
| DB 컬럼 | `schema.sql`, Entity/DTO, Mapper XML, AdminController |
| API 응답 | Controller, DTO, 관련 JS |
| 로그인 보호 | `WebConfig`, `LoginCheckInterceptor` |
| 외부 API | `application.yml`, `*Properties`, Service |
| 로그 정책 | `logback-spring.xml`, Interceptor, AOP |
| 화면 구조 | JSP, CSS, 관련 JS |
