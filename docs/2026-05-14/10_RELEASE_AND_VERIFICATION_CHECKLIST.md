# 릴리즈 및 검증 체크리스트

작성일: 2026-05-14

## 1. 빌드 전 확인

| 체크 | 상태 |
|---|---|
| Java 21 사용 | 확인 필요 |
| MySQL 실행 | 확인 필요 |
| DB 계정 `ssafy/ssafy` | 확인 필요 |
| 포트 8080 비어 있음 | 확인 필요 |
| API 키 설정 | 확인 필요 |
| 작업 중인 Java 프로세스 종료 | 확인 필요 |

## 2. 빌드

```powershell
.\mvnw.cmd -q clean package
```

성공 기준:

- Maven exit code 0
- `target/ssafyhome-0.0.1-SNAPSHOT.war` 생성
- ApplicationContext test 통과

## 3. 실행

```powershell
java -Dfile.encoding=UTF-8 -jar target\ssafyhome-0.0.1-SNAPSHOT.war
```

성공 로그:

```text
Started SsafyhomeApplication
Tomcat started on port 8080
```

## 4. 서버 확인

```powershell
curl.exe -s -o NUL -w "%{http_code}" http://localhost:8080/
```

예상: `200`

## 5. 페이지 확인

| 페이지 | URL | 기대 |
|---|---|---|
| 홈 | `/` | 서비스 hero 표시 |
| 실거래 | `/deals` | 검색/수집 form 표시 |
| 단지 | `/houses` | 지역/단지 검색 form 표시 |
| 회원 | `/members` | 회원가입/로그인 form 표시 |
| 관심지역 | `/favorites` | 미로그인 시 로그인 페이지 redirect |
| 공지 | `/notices` | 공지 화면 표시 |
| 지역 | `/regions` | 지역 API 화면 표시 |
| 관리자 | `/admin` | 미로그인 시 로그인 페이지 redirect |

## 6. API 확인

### 공개 API

```powershell
curl.exe -s "http://localhost:8080/api/deals/summary?lawdCd=11110&dealYmd=202407"
curl.exe -s --get --data-urlencode "keyword=현대" http://localhost:8080/api/houses
curl.exe -s "http://localhost:8080/api/regions/sido"
```

### 로그인 보호 API

```powershell
curl.exe -s -i "http://localhost:8080/api/favorites"
curl.exe -s -i "http://localhost:8080/api/admin/overview"
```

예상:

```text
HTTP/1.1 401
LOGIN_REQUIRED
```

## 7. 로그인 후 확인

PowerShell 예:

```powershell
$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$body = @{
  email = "verify@example.com"
  password = "1234"
  name = "검증"
  phone = "010-0000-0000"
  address = "서울"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method Post -ContentType "application/json" -Body $body -WebSession $session
Invoke-RestMethod -Uri "http://localhost:8080/api/admin/overview" -WebSession $session
```

## 8. 로그 확인

```powershell
Get-Content logs\ssafyhome.log -Encoding UTF8 -Tail 100
Get-Content logs\ssafyhome-error.log -Encoding UTF8 -Tail 100
```

필수 키워드:

- `REQUEST_START`
- `REQUEST_END`
- `SERVICE_CALL`
- `SERVICE_RETURN`
- `AUTH_REQUIRED`
- `AUTH_PASS`

## 9. 관리자 확인

로그인 후 `/admin` 접속.

확인 항목:

| 항목 | 기대 |
|---|---|
| metric 카드 | 회원/실거래/단지/관심지역 표시 |
| 거래 유형별 현황 | deal_type별 건수 |
| 작업 로그 | 최근 작업 로그 표시 |
| DB 조회 | 테이블 선택/키워드 검색 가능 |
| 최근 수집 실거래 | 최신 `property_deals` 표시 |

## 10. 외부 API 확인

data.go.kr:

```powershell
curl.exe -s -X POST "http://localhost:8080/api/deals/fetch?type=APT_TRADE&lawdCd=11110&dealYmd=202407&numOfRows=10"
```

주의: 로그인 필요.

VWorld:

```powershell
curl.exe -s -i "http://localhost:8080/api/regions/vworld/sido"
```

현재 키 권한에 따라 `INCORRECT_KEY`가 반환될 수 있습니다. 이 경우 애플리케이션은 502 JSON으로 처리하는 것이 정상입니다.

## 11. 릴리즈 전 문서 확인

| 문서 | 확인 |
|---|---|
| `docs/ERD.md` | DB 변경 반영 |
| `docs/CODE_OVERVIEW.md` | 코드 구조 변경 반영 |
| `docs/ARCHITECTURE*.md` | 구조 변경 반영 |
| `docs/2026-05-14/*.md` | 현재 날짜 산출물 최신화 |

## 12. known risks

| 위험 | 대응 |
|---|---|
| 관리자 role 미분리 | 운영 전 ADMIN role 검사 추가 |
| 메모리 작업 로그 | 영속 로그 테이블 도입 |
| API key 노출 | 운영에서는 환경변수로 이동 |
| VWorld key 오류 | VWorld 콘솔에서 키 권한 확인 |
| 대용량 DB 조회 | 관리자 rows size 제한 유지 |
