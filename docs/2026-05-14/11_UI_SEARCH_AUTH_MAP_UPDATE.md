# UI/검색/인증/지도 수정 내역

작성일: 2026-05-14

## 요약

실사용 부동산 정보 서비스 기준으로 로그인/회원가입, 주거 단지 검색, 실거래 검색, 관심지역, 공지사항 화면의 사용 흐름을 정리했다. 고객 화면에서 회원목록조회는 제거하고 관리자 대시보드의 현재 DB 조회 기능에서만 확인하도록 역할을 분리했다.

## 화면 변경

| 화면 | 변경 내용 |
|---|---|
| 공통 헤더 | 일반 메뉴에서 `로그인/회원` 제거. 우측 상태 영역에 `로그인`, `회원가입`, `로그아웃` 버튼 배치 |
| 회원 화면 | 계정 안내와 내 정보 확인만 제공. 회원목록조회는 관리자 페이지로 분리 |
| 주거 단지 | 통합 검색어 한 칸으로 지역/동/법정동 코드/단지명/도로명 검색 |
| 실거래가 | 통합 검색어 한 칸으로 지역/동/단지명/주택명/거래유형/법정동 코드 검색 |
| 관심지역 | JSON 출력 대신 목록 표, 거래 보기, 삭제 버튼 제공 |
| 공지사항 | 2컬럼 배치를 제거하고 목록 우선, 하단 작성/수정 영역 구조로 변경 |
| 지도 | 모든 페이지에 같은 위치의 미니 지도 표시. 단지 상세 선택 시 해당 좌표로 갱신 |

## 검색과 중복 제거

- `GET /api/deals`에 `keyword` 파라미터를 추가했다.
- `DealMapper.search`는 `lawd_cd`, `umd_nm`, `house_name`, `house_type`, `jibun`, `road_name`, `deal_type`, 한글 거래유형명을 함께 검색한다.
- 같은 거래가 여러 번 저장되어도 `row_number() over (partition by ...)`로 DB 조회 결과에서 한 번만 반환한다.
- `deals.js`에서도 거래 핵심 속성 조합으로 한 번 더 중복을 제거한다.
- `HouseMapper.searchHouses`는 `concat(sgg_cd, umd_cd)` 검색을 추가했고, 기존 `apt_seq` 그룹 기준으로 단지 중복 표시를 막는다.

## SQL 영향

`src/main/resources/schema.sql`에 통합 검색 보조 인덱스를 추가했다.

```sql
index idx_property_deals_keyword (lawd_cd, umd_nm, house_name, deal_type)
```

기존 DB에 이미 테이블이 생성되어 있다면 아래 SQL을 수동 적용한다.

```sql
alter table property_deals
    add index idx_property_deals_keyword (lawd_cd, umd_nm, house_name, deal_type);
```

## 로그 영향

- `DealService.search` 로그에 `keyword`를 추가했다.
- `HouseService` 로그 메시지를 `주거 단지` 기준으로 수정했다.
- 관심지역 등록/삭제와 공지 등록/수정/삭제 작업 로그는 기존 `OperationLogService` 흐름을 유지한다.
- 콘솔 로그와 파일 로그는 `logback-spring.xml`의 동일 `LOG_PATTERN`을 공유한다.

## 검증

| 항목 | 결과 |
|---|---|
| `./mvnw.cmd -q clean package -DskipTests` | 성공 |
| `GET /` | 200 |
| `GET /houses` | 200 |
| `GET /api/houses?keyword=종로` | 성공 |
| `GET /api/deals?keyword=연립&dealYmd=202407` | 성공, 연립다세대 매매/전월세 반환 |
| `GET /api/favorites` 비로그인 | 401 `LOGIN_REQUIRED` |
| 브라우저 렌더링 | 로그인/회원가입 버튼, 인증 모달, 통합 검색, 미니 지도 확인 |
