# SSAFY Home Architecture

이 문서는 SSAFY Home 프로젝트의 기본 아키텍처를 글로 설명합니다.  
동일한 내용을 Mermaid 다이어그램으로만 보고 싶다면 `docs/ARCHITECTURE.mmd`를 확인합니다.

## 1. 아키텍처 개요

SSAFY Home은 Spring Boot MVC 기반의 부동산 정보 제공 웹 애플리케이션입니다.  
사용자는 JSP 화면에서 단지, 실거래가, 관심지역, 공지사항, 지역정보를 조회하고, 관리자는 footer 하단의 작은 관리자 링크를 통해 운영 대시보드로 이동합니다.

전체 흐름은 다음과 같습니다.

1. 브라우저가 JSP 화면을 요청합니다.
2. `PageController`가 JSP view name을 반환합니다.
3. JSP는 정적 JS/CSS를 로드합니다.
4. JS는 `/api/**` REST API를 호출합니다.
5. Controller는 요청을 받아 Service로 위임합니다.
6. Service는 비즈니스 로직, 외부 API 호출, MyBatis Mapper 호출을 담당합니다.
7. Mapper는 MySQL DB를 조회하거나 갱신합니다.
8. 공통 로그, 인증, 예외 처리는 Interceptor, AOP, ControllerAdvice가 담당합니다.

## 2. 주요 계층

| 계층 | 주요 파일/패키지 | 역할 |
|---|---|---|
| View | `src/main/webapp/WEB-INF/views/*.jsp` | 사용자 화면과 관리자 화면 |
| Static | `src/main/resources/static/css`, `src/main/resources/static/js` | UI 스타일, API 호출, 테이블 렌더링, 다크모드 |
| Page Controller | `PageController` | URL을 JSP로 연결 |
| REST Controller | `member`, `deal`, `house`, `favorite`, `notice`, `region`, `common.AdminController` | 브라우저 API 요청 처리 |
| Service | 각 도메인의 `*Service` | 비즈니스 로직, 검증, 외부 API 호출 |
| Mapper | `*Mapper.java`, `mapper/*.xml` | MyBatis 기반 SQL 실행 |
| Database | MySQL `ssafy_home` | 회원, 실거래, 단지, 관심지역, 공지, 지역 코드 저장 |
| Config | `WebConfig`, `application.yml`, `logback-spring.xml` | Bean, Interceptor, DB, 로그 설정 |
| Cross Cutting | `RequestLoggingInterceptor`, `LoginCheckInterceptor`, `ServiceLoggingAspect`, `GlobalExceptionHandler` | 요청 로그, 로그인 보호, Service 로그, 전역 예외 처리 |

## 3. 도메인 구성

| 도메인 | 설명 |
|---|---|
| member | 회원가입, 로그인, 로그아웃, 현재 사용자 조회, 회원 관리 |
| deal | data.go.kr 실거래 API 수집, DB 검색, 월간 시세 요약 |
| house | 기존 DB의 아파트 단지, 법정동, 단지별 거래 이력 조회 |
| favorite | 로그인 사용자별 관심지역 등록/조회/삭제 |
| notice | 공지사항 CRUD와 조회수 증가 |
| region | DB 지역 코드 조회, VWorld/SGIS 외부 지역 API 호출 |
| common/admin | 공통 응답, 전역 예외, 운영 로그, 관리자 대시보드 |

## 4. 데이터 저장소

MySQL에는 다음 핵심 테이블이 있습니다.

| 테이블 | 역할 |
|---|---|
| `members` | 회원 계정과 로그인 사용자 정보 |
| `favorites` | 회원별 관심지역 |
| `notices` | 공지사항 |
| `dongcodes` | 법정동 코드 |
| `houseinfos` | 아파트 단지 기본정보 |
| `housedeals` | 단지별 거래 이력 |
| `property_deals` | 외부 API로 수집한 실거래 통합 데이터 |

주요 관계는 `members -> favorites`, `members -> notices`, `houseinfos -> housedeals`입니다.

## 5. 외부 API

| 외부 서비스 | 사용 위치 | 목적 |
|---|---|---|
| data.go.kr | `DealService` | 아파트/연립다세대 매매·전월세 실거래 수집 |
| VWorld | `RegionService` | 시도/시군구/동 행정구역 API 확인 |
| SGIS | `RegionService` | SGIS 토큰 발급 확인 |

외부 API 오류는 `ExternalApiException`으로 감싸고 `GlobalExceptionHandler`가 502 응답으로 변환합니다.

## 6. 인증과 로그

로그인 상태는 `HttpSession`의 `LOGIN_MEMBER_ID`로 관리합니다.  
`LoginCheckInterceptor`는 `/favorites`, `/admin`, `/api/admin/**`, `/api/favorites/**`, `/api/logs/**`, `/api/deals/fetch*` 요청을 보호합니다.

로그는 다음 세 층에서 남습니다.

| 로그 | 담당 | 내용 |
|---|---|---|
| 요청 로그 | `RequestLoggingInterceptor` | 요청 시작/종료, status, duration, session, handler |
| Service 로그 | `ServiceLoggingAspect` | Service 메서드 호출 파라미터와 반환값 |
| 작업 로그 | `OperationLogService` | 회원/관심지역/공지/데이터 수집 같은 사용자 작업 |

로그는 IDE 콘솔, `logs/ssafyhome.log`, `logs/ssafyhome-error.log`에 저장됩니다.

## 7. 전체 흐름 요약

```text
Browser
  -> JSP View
  -> static JS fetch()
  -> REST Controller
  -> Service
  -> MyBatis Mapper
  -> MySQL

공통 처리:
  RequestLoggingInterceptor
  LoginCheckInterceptor
  ServiceLoggingAspect
  GlobalExceptionHandler
  Logback file logging
```
