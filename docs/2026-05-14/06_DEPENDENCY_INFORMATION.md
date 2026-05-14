# 의존성 정보 문서

작성일: 2026-05-14  
기준 파일: `pom.xml`

## 1. 프로젝트 기본 정보

| 항목 | 값 |
|---|---|
| groupId | `com.ssafy` |
| artifactId | `ssafyhome` |
| version | `0.0.1-SNAPSHOT` |
| packaging | `war` |
| Spring Boot parent | `3.5.14` |
| Java | `21` |

## 2. Runtime/Compile 의존성

| 의존성 | Scope | 용도 | 제거 시 영향 |
|---|---|---|---|
| `spring-boot-starter-validation` | compile | `@Valid`, Bean Validation | DTO 검증 실패, Controller 요청값 검증 불가 |
| `spring-boot-starter-web` | compile | Spring MVC, REST Controller, 내장 Tomcat | 웹 서버/API/JSP 라우팅 불가 |
| `spring-boot-starter-aop` | compile | `@Aspect`, `@Before`, `@AfterReturning` | Service AOP 로그 미동작 |
| `spring-boot-devtools` | runtime optional | 개발 중 자동 재시작 | 운영 영향 작음, 개발 편의 감소 |
| `tomcat-embed-jasper` | compile | JSP 렌더링 | `/WEB-INF/views/*.jsp` 처리 실패 |
| `mybatis-spring-boot-starter` | compile | MyBatis, Mapper scan, SqlSessionTemplate | DB Mapper 동작 실패 |
| `spring-security-crypto` | compile | BCrypt password hash | 로그인 비밀번호 검증 불가 |
| `lombok` | optional | `@Slf4j`, `@RequiredArgsConstructor` | 컴파일 시 생성 코드 없음 |
| `mysql-connector-j` | runtime | MySQL JDBC 연결 | DB 연결 실패 |

## 3. Test 의존성

| 의존성 | Scope | 용도 |
|---|---|---|
| `spring-boot-starter-test` | test | JUnit, Spring test, ApplicationContext 테스트 |
| `mybatis-spring-boot-starter-test` | test | MyBatis Mapper 테스트 보조 |

## 4. 플러그인

| 플러그인 | 용도 |
|---|---|
| `spring-boot-maven-plugin` | 실행 가능한 WAR 패키징 |

Lombok은 최종 산출물에서 제외됩니다. Lombok은 컴파일 시점에 생성자를 만들고 로그 필드를 생성하는 도구이기 때문입니다.

## 5. 의존성 선택 이유

### Spring Web

Controller, REST API, JSP page routing의 기반입니다.  
이 프로젝트는 API 서버와 JSP View 서버가 같은 애플리케이션 안에 있습니다.

### MyBatis

SSAFY Home 데이터는 기존 SQL 스키마와 대용량 조회가 중요합니다.  
JPA보다 SQL을 직접 제어하는 MyBatis가 학습 목적과 현재 구조에 더 맞습니다.

### Jasper

Spring Boot는 기본적으로 JSP 렌더링 엔진을 자동 포함하지 않습니다.  
`tomcat-embed-jasper`가 있어야 `.jsp`를 해석할 수 있습니다.

### AOP

Service 전체에 공통 로그를 자동 적용하기 위해 필요합니다.  
각 Service마다 로그를 직접 쓰지 않아도 호출/반환 로그를 일관되게 남길 수 있습니다.

### Security Crypto

Spring Security 전체 인증 필터 체인은 쓰지 않지만, 비밀번호 저장에는 BCrypt가 필요합니다.  
따라서 `spring-security-crypto`만 사용합니다.

## 6. 버전 확인 명령

```powershell
.\mvnw.cmd dependency:list "-DincludeArtifactIds=lombok,mysql-connector-j,spring-boot-devtools,spring-boot-starter-web,spring-boot-starter-aop"
```

현재 확인된 주요 resolved dependency:

| artifact | version |
|---|---|
| `spring-boot-starter-web` | `3.5.14` |
| `spring-boot-starter-aop` | `3.5.14` |
| `spring-boot-devtools` | `3.5.14` |
| `lombok` | Maven resolved version |
| `mysql-connector-j` | Maven resolved version |

## 7. 의존성 변경 시 체크

| 변경 | 체크 |
|---|---|
| Spring Boot 버전 변경 | Tomcat/Jackson/Validation/MyBatis 호환성 |
| MyBatis 버전 변경 | Mapper scan, XML DTD, 테스트 |
| MySQL Driver 변경 | DB 연결, 시간대, 문자셋 |
| Jasper 제거 | JSP 렌더링 실패 |
| Lombok 제거 | 생성자, log 필드 컴파일 오류 |
| AOP 제거 | `ServiceLoggingAspect` Bean/annotation 처리 오류 |
