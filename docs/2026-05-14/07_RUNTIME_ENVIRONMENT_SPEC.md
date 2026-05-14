# 실행환경 스펙 문서

작성일: 2026-05-14

## 1. 기본 실행 환경

| 항목 | 값 |
|---|---|
| OS | Windows 환경 기준 |
| Shell | PowerShell |
| Java | JDK 21 |
| Build Tool | Maven Wrapper |
| Spring Boot | 3.5.14 |
| Packaging | WAR |
| WAS | Embedded Tomcat |
| 기본 포트 | 8080 |
| Timezone | Asia/Seoul |
| DB | MySQL |

## 2. 권장 Java

```powershell
java -version
```

필요:

```text
Java 21.x
```

## 3. DB 환경

| 항목 | 기본값 |
|---|---|
| DB host | localhost |
| port | 3306 |
| database | ssafy_home |
| username | ssafy |
| password | ssafy |
| driver | `com.mysql.cj.jdbc.Driver` |

JDBC URL:

```text
jdbc:mysql://localhost:3306/ssafy_home?createDatabaseIfNotExist=true&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
```

## 4. 환경변수

| 변수 | 설명 | 기본값 |
|---|---|---|
| `DB_URL` | MySQL JDBC URL | application.yml 기본값 |
| `DB_USERNAME` | DB 계정 | `ssafy` |
| `DB_PASSWORD` | DB 비밀번호 | `ssafy` |
| `PUBLIC_DATA_SERVICE_KEY` | data.go.kr 인증키 | yml 기본값 |
| `VWORLD_KEY` | VWorld 인증키 | yml 기본값 |
| `SGIS_CONSUMER_KEY` | SGIS consumer key | yml 기본값 |
| `SGIS_CONSUMER_SECRET` | SGIS consumer secret | yml 기본값 |

운영에서는 API 키를 yml에 직접 두기보다 환경변수로 분리하는 것을 권장합니다.

## 5. 빌드 명령

```powershell
.\mvnw.cmd -q clean package
```

빌드 산출물:

```text
target/ssafyhome-0.0.1-SNAPSHOT.war
```

## 6. 실행 명령

```powershell
java -Dfile.encoding=UTF-8 -jar target\ssafyhome-0.0.1-SNAPSHOT.war
```

`-Dfile.encoding=UTF-8`은 한글 로그와 JSP 응답의 인코딩 문제를 줄이기 위해 권장합니다.

## 7. 백그라운드 실행 예

```powershell
$out = Join-Path (Get-Location) 'target\app-run.out.log'
$err = Join-Path (Get-Location) 'target\app-run.err.log'
Start-Process -FilePath java `
  -ArgumentList @('-Dfile.encoding=UTF-8','-jar','target\ssafyhome-0.0.1-SNAPSHOT.war') `
  -RedirectStandardOutput $out `
  -RedirectStandardError $err `
  -WindowStyle Hidden
```

## 8. 포트 확인

```powershell
Get-NetTCPConnection -LocalPort 8080
```

## 9. 프로세스 확인

```powershell
Get-CimInstance Win32_Process |
  Where-Object { $_.Name -eq 'java.exe' -and $_.CommandLine -like '*ssafyhome-0.0.1-SNAPSHOT.war*' } |
  Select-Object ProcessId, CommandLine
```

## 10. 서버 중지

```powershell
Get-CimInstance Win32_Process |
  Where-Object { $_.Name -eq 'java.exe' -and $_.CommandLine -like '*ssafyhome-0.0.1-SNAPSHOT.war*' } |
  ForEach-Object { Stop-Process -Id $_.ProcessId -Force }
```

## 11. 접속 URL

| URL | 설명 |
|---|---|
| `http://localhost:8080/` | 홈 |
| `http://localhost:8080/deals` | 실거래 |
| `http://localhost:8080/houses` | 단지 검색 |
| `http://localhost:8080/members` | 로그인/회원 |
| `http://localhost:8080/admin` | 관리자 |

## 12. smoke test

```powershell
curl.exe -s -o NUL -w "%{http_code}" http://localhost:8080/
curl.exe -s "http://localhost:8080/api/deals/summary?lawdCd=11110&dealYmd=202407"
curl.exe -s -i "http://localhost:8080/api/favorites"
```

예상:

- `/`는 200
- `/api/deals/summary`는 200
- `/api/favorites` 비로그인은 401

## 13. 실행 전 체크리스트

| 체크 | 기준 |
|---|---|
| MySQL 실행 | 3306 listening |
| DB 계정 | `ssafy/ssafy` 접속 가능 |
| Java | 21 |
| 포트 | 8080 비어 있음 |
| API 키 | 필요한 경우 환경변수 설정 |
| 로그 폴더 | `logs/` 자동 생성 가능 |
