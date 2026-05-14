# SSAFY Home Documentation Index

작성일: 2026-05-14  
대상 프로젝트: `C:\SSAFY\joseph\workspaces\prj0514\temp`  
현재 기준: Spring Boot 3.5.14, Java 21, JSP MVC, MyBatis, MySQL

## 문서 구성

| 파일 | 목적 |
|---|---|
| `01_SERVICE_DESCRIPTION.md` | 서비스 설명서. 사용자/관리자 관점의 기능과 현재 서비스 상태 |
| `02_CODE_EXPLANATION.md` | 코드 설명서. 패키지, 파일, 계층별 역할과 변경 영향 |
| `03_MAINTENANCE_AND_OPERATIONS.md` | 유지보수 및 운영 문서. 실행, 로그, 장애 대응, 백업/관리 포인트 |
| `04_API_SPECIFICATION.md` | API 명세서. 화면 라우트와 REST API 목록, 파라미터, 응답 구조 |
| `05_DATABASE_COLUMN_DEFINITION.md` | 컬럼정의서. 테이블별 컬럼, 타입, 제약, 관계, 인덱스 |
| `06_DEPENDENCY_INFORMATION.md` | 의존성 정보 문서. Maven dependency별 용도와 영향 |
| `07_RUNTIME_ENVIRONMENT_SPEC.md` | 실행환경 스펙 문서. JDK, DB, 포트, 환경변수, 실행 명령 |
| `08_ARCHITECTURE_CURRENT_STATE.md` | 현재 아키텍처 문서. 계층, 요청 흐름, 데이터 흐름, Mermaid 포함 |
| `09_LOGGING_ERROR_SECURITY_GUIDE.md` | 로그/에러/보안 문서. Interceptor, AOP, GlobalExceptionHandler, 세션 정책 |
| `10_RELEASE_AND_VERIFICATION_CHECKLIST.md` | 릴리즈/검증 체크리스트. 빌드, smoke test, UI/API/로그 확인 |

## 현재 상태 요약

SSAFY Home은 실거래 API 수집, 기존 아파트 단지 DB 검색, 회원/관심지역/공지사항, 관리자 대시보드를 제공하는 Spring Boot MVC 웹 애플리케이션입니다.  
정적 HTML은 JSP로 전환되었고, ViewResolver가 `/WEB-INF/views/*.jsp`를 찾도록 설정되어 있습니다.

핵심 기술:

- Spring Boot 3.5.14
- Java 21
- JSP + embedded Jasper
- Spring Web MVC
- MyBatis + MySQL
- Lombok
- Spring AOP
- Session 기반 로그인
- Interceptor 기반 요청 로깅/로그인 보호
- Logback 기반 콘솔/파일/에러 로그 분리

## 빠른 실행

```powershell
.\mvnw.cmd -q clean package
java -Dfile.encoding=UTF-8 -jar target\ssafyhome-0.0.1-SNAPSHOT.war
```

접속:

```text
http://localhost:8080
```

## 현재 중요 이슈

| 항목 | 상태 |
|---|---|
| MySQL 계정 | 기본값 `ssafy / ssafy` |
| DB 이름 | `ssafy_home` |
| VWorld API | 현재 설정 키로 실제 API가 `INCORRECT_KEY`를 반환할 수 있음. 키 권한/도메인/API 사용 설정 확인 필요 |
| 관리자 접근 | 로그인 필요. 상단 메뉴에는 숨김, footer 하단 작은 `관리자` 링크로 접근 |
| 로그 파일 | `logs/ssafyhome.log`, `logs/ssafyhome-error.log` |
| 작업 로그 | 메모리 기반 `OperationLogService`, 서버 재시작 시 초기화 |
