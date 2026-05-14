# API 명세서

작성일: 2026-05-14

## 1. 공통 응답

### 정상 응답

```json
{
  "success": true,
  "message": "OK",
  "data": {}
}
```

### 오류 응답

```json
{
  "success": false,
  "message": "오류 메시지",
  "code": "ERROR_CODE",
  "status": 400,
  "path": "/api/example",
  "timestamp": "2026-05-14T11:00:00"
}
```

## 2. 페이지 라우트

| Method | URL | View | 설명 | 로그인 |
|---|---|---|---|---|
| GET | `/` | `home.jsp` | 홈 | 불필요 |
| GET | `/deals` | `deals.jsp` | 실거래가 | 불필요 |
| GET | `/houses` | `houses.jsp` | 아파트 단지 | 불필요 |
| GET | `/members` | `members.jsp` | 로그인/회원 | 불필요 |
| GET | `/favorites` | `favorites.jsp` | 관심지역 | 필요 |
| GET | `/notices` | `notices.jsp` | 공지사항 | 불필요 |
| GET | `/regions` | `regions.jsp` | 지역정보 | 불필요 |
| GET | `/admin` | `admin.jsp` | 관리자 대시보드 | 필요 |

## 3. Auth API

Base URL: `/api/auth`

### POST `/register`

회원가입 후 자동 로그인합니다.

Request:

```json
{
  "email": "test@ssafy.com",
  "password": "1234",
  "name": "김싸피",
  "phone": "010-0000-0000",
  "address": "서울"
}
```

Response: `ApiResponse<MemberDto>`

### POST `/login`

Request:

```json
{
  "email": "test@ssafy.com",
  "password": "1234"
}
```

Response: `ApiResponse<MemberDto>`

### POST `/logout`

현재 세션을 무효화합니다.

Response: `ApiResponse<Void>`

### GET `/me`

현재 로그인 사용자를 조회합니다.

로그인 필요: 세션 없으면 401

## 4. Member API

Base URL: `/api/members`

| Method | URL | Query/Body | 설명 |
|---|---|---|---|
| GET | `/api/members` | `keyword` optional | 회원 목록 검색 |
| GET | `/api/members/{id}` | path `id` | 회원 상세 |
| PUT | `/api/members/{id}` | UpdateRequest JSON | 회원 수정 |
| DELETE | `/api/members/{id}` | path `id` | 회원 삭제 |

UpdateRequest:

```json
{
  "password": "1234",
  "name": "김싸피",
  "phone": "010-0000-0000",
  "address": "서울"
}
```

## 5. Deal API

Base URL: `/api/deals`

### POST `/fetch`

선택한 거래유형/지역/월 데이터를 외부 API에서 수집합니다.

Query:

| 이름 | 필수 | 예 | 설명 |
|---|---|---|---|
| type | Y | `APT_TRADE` | 거래유형 |
| lawdCd | Y | `11110` | 법정동 앞 5자리 |
| dealYmd | Y | `202407` | 계약년월 |
| numOfRows | N | `50` | API 요청 건수 |

로그인 필요.

### POST `/fetch-all`

4개 거래유형을 한 번에 수집합니다.

Query:

| 이름 | 필수 | 예 |
|---|---|---|
| lawdCd | Y | `11110` |
| dealYmd | Y | `202407` |
| numOfRows | N | `50` |

로그인 필요.

### GET `/summary`

월간 시세 요약.

Query:

| 이름 | 필수 | 예 |
|---|---|---|
| lawdCd | Y | `11110` |
| dealYmd | Y | `202407` |

### GET `/api/deals`

DB에 저장된 실거래 검색.

Query:

| 이름 | 필수 | 설명 |
|---|---|---|
| dealType | N | 거래유형 |
| lawdCd | N | 지역 코드 |
| dong | N | 동명 |
| houseName | N | 단지/주택명 |
| dealYmd | N | 계약년월 |

## 6. House API

Base URL: `/api/houses`

| Method | URL | Query | 설명 |
|---|---|---|---|
| GET | `/dongcodes` | `keyword` | 법정동 검색 |
| GET | `/api/houses` | `keyword`, `dongCode` | 단지 검색 |
| GET | `/{aptSeq}` | path | 단지 상세 |
| GET | `/{aptSeq}/deals` | path | 단지 거래 이력 |

## 7. Favorite API

Base URL: `/api/favorites`

로그인 필요.

| Method | URL | Body | 설명 |
|---|---|---|---|
| GET | `/api/favorites` | 없음 | 내 관심지역 목록 |
| POST | `/api/favorites` | Favorite Request | 관심지역 등록 |
| DELETE | `/api/favorites/{id}` | 없음 | 관심지역 삭제 |

Request:

```json
{
  "sidoNm": "서울특별시",
  "sigunguNm": "종로구",
  "dongNm": "무악동",
  "lawdCd": "11110",
  "memo": "관심 지역"
}
```

## 8. Notice API

Base URL: `/api/notices`

| Method | URL | Query/Body | 설명 |
|---|---|---|---|
| GET | `/api/notices` | `keyword` optional | 공지 목록 |
| GET | `/api/notices/{id}` | path | 공지 상세, 조회수 증가 |
| POST | `/api/notices` | Request JSON | 공지 작성 |
| PUT | `/api/notices/{id}` | Request JSON | 공지 수정 |
| DELETE | `/api/notices/{id}` | path | 공지 삭제 |

Request:

```json
{
  "title": "공지 제목",
  "content": "공지 내용"
}
```

## 9. Region API

Base URL: `/api/regions`

| Method | URL | Query | 설명 |
|---|---|---|---|
| GET | `/sido` | 없음 | DB 시도 목록 |
| GET | `/sigungu` | `sidoCode` | DB 시군구 목록 |
| GET | `/dong` | `sigunguCode` | DB 동 목록 |
| GET | `/sgis-token` | 없음 | SGIS 토큰 응답 |
| GET | `/vworld/sido` | 없음 | VWorld 시도 |
| GET | `/vworld/sigungu` | `sidoCode` | VWorld 시군구 |
| GET | `/vworld/dong` | `sigunguCode` | VWorld 동 |

## 10. Admin API

Base URL: `/api/admin`

로그인 필요.

| Method | URL | Query | 설명 |
|---|---|---|---|
| GET | `/overview` | 없음 | 대시보드 요약 |
| GET | `/tables` | 없음 | 조회 가능한 테이블 목록 |
| GET | `/rows` | `table`, `keyword`, `size` | 테이블 데이터 조회 |

`/rows` 허용 테이블:

- `members`
- `property_deals`
- `houseinfos`
- `housedeals`
- `dongcodes`
- `favorites`
- `notices`

## 11. Operation Log API

Base URL: `/api/logs`

로그인 필요.

| Method | URL | Query | 설명 |
|---|---|---|---|
| GET | `/api/logs` | `category` optional | 작업 로그 조회 |

category 예:

- `deals`
- `members`
- `favorites`
- `notices`

## 12. 상태 코드

| Status | 의미 |
|---:|---|
| 200 | 정상 |
| 201 | 생성 |
| 400 | 요청값 검증 실패 |
| 401 | 로그인 필요 |
| 404 | 데이터 없음 |
| 502 | 외부 API 실패 |
| 500 | 서버 내부 오류 |
