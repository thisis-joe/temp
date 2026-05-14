# 코드 설명서

작성일: 2026-05-14

## 1. 코드 구조 요약

```text
src/main/java/com/ssafy/home
├─ common      공통 응답, 예외, 관리자, 로그, AOP
├─ config      Spring MVC 설정, Interceptor
├─ member      회원/로그인
├─ deal        실거래 API 수집/검색/요약
├─ house       기존 DB 단지/거래 조회
├─ favorite    관심지역
├─ notice      공지사항
├─ region      지역 코드와 외부 지역 API
├─ PageController.java
└─ SsafyhomeApplication.java
```

## 2. 시작점

| 파일 | 설명 | 영향 |
|---|---|---|
| `SsafyhomeApplication.java` | Spring Boot 실행 클래스 | 전체 component scan, 설정 로딩, 내장 Tomcat 실행 |
| `PageController.java` | JSP 화면 라우팅 | URL과 JSP 파일 연결 |

`PageController`는 REST API가 아니라 View 반환 전용입니다.

## 3. common 패키지

| 파일 | 역할 | 주요 영향 |
|---|---|---|
| `ApiResponse.java` | 정상 응답 공통 포맷 | 모든 JS의 API 파싱 방식에 영향 |
| `ErrorResponse.java` | 오류 응답 공통 포맷 | 브라우저 오류 표시와 HTTP 상태 코드 표시 |
| `ExternalApiException.java` | 외부 API 실패 전용 예외 | 502 응답으로 변환 |
| `GlobalExceptionHandler.java` | 전역 예외 처리 | Controller별 try/catch 제거, 오류 응답 통일 |
| `OperationLog.java` | 작업 로그 record | 관리자 로그 표시 데이터 구조 |
| `OperationLogService.java` | 작업 로그 저장/조회 | 메모리 로그와 파일 로그 기록 |
| `OperationLogController.java` | `/api/logs` 조회 | 관리자 로그 필터링 |
| `AdminController.java` | 관리자 대시보드 API | DB 현황, 테이블 조회, 최근 거래 표시 |
| `ServiceLoggingAspect.java` | Service AOP 로그 | 모든 Service 호출/반환 로그 |

### 주의

`OperationLogService`의 작업 로그는 메모리 기반입니다. 서버를 재시작하면 사라집니다. 영속 로그가 필요하면 DB 테이블로 분리해야 합니다.

## 4. config 패키지

| 파일 | 역할 |
|---|---|
| `WebConfig.java` | `RestClient` Bean 등록, Interceptor 등록 |
| `RequestLoggingInterceptor.java` | 요청 시작/종료/오류 로그, MDC requestId |
| `LoginCheckInterceptor.java` | 로그인 필요한 페이지/API 보호 |

보호 경로:

- `/favorites`
- `/admin`
- `/api/admin/**`
- `/api/favorites/**`
- `/api/logs/**`
- `/api/deals/fetch`
- `/api/deals/fetch-all`

## 5. member 패키지

| 파일 | 설명 |
|---|---|
| `AuthController.java` | 회원가입, 로그인, 로그아웃, 내 정보 |
| `MemberController.java` | 회원 목록/상세/수정/삭제 |
| `MemberService.java` | BCrypt 암호화, 로그인 검증, 회원 비즈니스 |
| `MemberMapper.java` | MyBatis 인터페이스 |
| `Member.java` | DB Entity 성격의 POJO |
| `MemberDto.java` | 요청/응답 DTO |

세션 키:

```java
LOGIN_MEMBER_ID
```

로그인 성공 시 이 값이 `HttpSession`에 저장됩니다.

## 6. deal 패키지

| 파일 | 설명 |
|---|---|
| `DealController.java` | 실거래 수집, 통합 수집, 검색, 요약 API |
| `DealService.java` | 외부 API 호출, XML 파싱, DB 저장/삭제/조회 |
| `DealMapper.java` | MyBatis 실거래 Mapper |
| `DealType.java` | 거래 유형 enum |
| `PropertyDeal.java` | `property_deals` 매핑 객체 |
| `DealFetchResult.java` | 수집 결과 응답 |
| `DealSummary.java` | 월간 요약 결과 |
| `PublicDataProperties.java` | data.go.kr 키 바인딩 |

핵심 흐름:

```text
DealController -> DealService -> data.go.kr -> XML parse -> DealMapper -> property_deals
```

## 7. house 패키지

| 파일 | 설명 |
|---|---|
| `HouseController.java` | 법정동/단지/상세/거래 API |
| `HouseService.java` | 기존 DB 조회 비즈니스 |
| `HouseMapper.java` | MyBatis 단지 조회 Mapper |
| `DongCodeDto.java` | 법정동 검색 결과 |
| `HouseInfoDto.java` | 단지 목록 결과 |
| `HouseDetailDto.java` | 단지 상세와 거래 이력 |
| `HouseDealDto.java` | 단지 거래 이력 |

주요 테이블:

- `dongcodes`
- `houseinfos`
- `housedeals`

## 8. favorite 패키지

| 파일 | 설명 |
|---|---|
| `FavoriteController.java` | 관심지역 목록/등록/삭제 |
| `FavoriteService.java` | 회원별 관심지역 처리 |
| `FavoriteMapper.java` | MyBatis 관심지역 Mapper |
| `Favorite.java` | favorites 매핑 객체 |
| `FavoriteDto.java` | 요청/응답 DTO |

삭제 SQL은 `id`와 `member_id`를 함께 사용합니다. 다른 회원의 데이터를 삭제하지 못하게 하기 위한 구조입니다.

## 9. notice 패키지

| 파일 | 설명 |
|---|---|
| `NoticeController.java` | 공지 CRUD API |
| `NoticeService.java` | 공지 비즈니스와 조회수 증가 |
| `NoticeMapper.java` | MyBatis 공지 Mapper |
| `Notice.java` | notices 매핑 객체 |
| `NoticeDto.java` | 요청/응답 DTO |

공지 상세 조회 시 `increaseViewCount`가 실행됩니다.

## 10. region 패키지

| 파일 | 설명 |
|---|---|
| `RegionController.java` | DB 지역, VWorld, SGIS API |
| `RegionService.java` | 지역 조회와 외부 API 호출 |
| `RegionMapper.java` | MyBatis 지역 Mapper |
| `RegionCodeDto.java` | 지역 코드 DTO |
| `SgisProperties.java` | SGIS 키 바인딩 |
| `VWorldProperties.java` | VWorld 키 바인딩 |

VWorld 또는 SGIS 오류는 외부 API 오류로 처리됩니다.

## 11. resources

| 파일/폴더 | 설명 |
|---|---|
| `application.yml` | DB, JSP, MyBatis, API 키, 세션 설정 |
| `logback-spring.xml` | 콘솔/파일/에러 로그 설정 |
| `schema.sql` | 테이블 생성 |
| `mapper/*.xml` | MyBatis SQL |
| `static/css/app.css` | 전체 UI 스타일 |
| `static/js/*.js` | 화면별 API 호출과 렌더링 |

## 12. JSP

| 파일 | 설명 |
|---|---|
| `home.jsp` | 서비스 홈 |
| `deals.jsp` | 실거래 화면 |
| `houses.jsp` | 단지 검색 화면 |
| `members.jsp` | 로그인/회원 화면 |
| `favorites.jsp` | 관심지역 화면 |
| `notices.jsp` | 공지사항 화면 |
| `regions.jsp` | 지역정보 화면 |
| `admin.jsp` | 관리자 대시보드 |

## 13. 변경 영향 빠른 표

| 변경 | 같이 볼 파일 |
|---|---|
| DB 컬럼 변경 | `schema.sql`, Entity, DTO, Mapper XML, `AdminController` |
| API 응답 변경 | Controller, DTO, 관련 JS |
| 화면 문구/배치 변경 | JSP, CSS |
| 버튼 동작 변경 | 관련 JS, Controller |
| 로그인 보호 변경 | `WebConfig`, `LoginCheckInterceptor` |
| 로그 정책 변경 | `logback-spring.xml`, Interceptor, AOP |
| 외부 API 키 변경 | `application.yml`, Properties, Service |
