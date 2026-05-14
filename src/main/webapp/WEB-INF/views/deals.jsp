<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>실거래가 | SSAFY Home</title>
    <link rel="stylesheet" href="/css/app.css">
</head>
<body>
<header class="topbar">
    <a class="brand" href="/">SSAFY Home</a>
    <nav class="nav">
        <a class="active" href="/deals">실거래가</a>
        <a href="/houses">아파트 단지</a>
        <a href="/members">로그인/회원</a>
        <a href="/favorites">관심지역</a>
        <a href="/notices">공지사항</a>
        <a href="/regions">지역정보</a>
        <a href="/admin">관리자</a>
    </nav>
    <div class="header-tools"><span id="sessionStatus">로그인 확인 중</span><button id="themeToggle" class="ghost small">다크 모드</button></div>
</header>

<main class="page">
    <section class="page-head">
        <h1>실거래가 검색</h1>
        <p>법정동 코드와 계약월로 공공데이터 실거래 API를 수집하고, 매매와 전월세 흐름을 요약합니다.</p>
    </section>

    <section class="panel search-panel">
        <div class="form-grid">
            <label>거래 유형
                <select id="dealType">
                    <option value="APT_TRADE">아파트 매매</option>
                    <option value="APT_RENT">아파트 전월세</option>
                    <option value="RH_TRADE">연립다세대 매매</option>
                    <option value="RH_RENT">연립다세대 전월세</option>
                </select>
            </label>
            <label>법정동 코드 <input id="lawdCd" value="11110" maxlength="5"></label>
            <label>계약 년월 <input id="dealYmd" value="202407" maxlength="6"></label>
            <label>단지명 <input id="houseName" placeholder="예: 현대"></label>
        </div>
        <div class="actions">
            <button id="searchDeals">DB 검색</button>
            <button id="fetchDeals" class="secondary">선택 유형 수집</button>
            <button id="fetchAllDeals" class="secondary">4개 API 통합 수집</button>
            <button id="loadSummary" class="ghost">시세 요약</button>
        </div>
    </section>

    <section class="panel">
        <h2>월간 시세 요약</h2>
        <div class="table-wrap">
            <table>
                <thead><tr><th>유형</th><th>건수</th><th>최저</th><th>평균</th><th>최고</th><th>평균 보증금</th><th>평균 월세</th><th>평균 면적</th><th>만원/㎡</th></tr></thead>
                <tbody id="summaryRows"><tr><td colspan="9">시세 요약 버튼을 누르세요.</td></tr></tbody>
            </table>
        </div>
    </section>

    <section class="panel">
        <h2>거래 목록</h2>
        <div class="table-wrap">
            <table>
                <thead><tr><th>유형</th><th>법정동</th><th>단지명</th><th>계약일</th><th>금액/보증금</th><th>월세</th><th>면적</th><th>층</th></tr></thead>
                <tbody id="dealRows"><tr><td colspan="8">검색 조건을 입력하고 조회하세요.</td></tr></tbody>
            </table>
        </div>
    </section>
</main>
<script src="/js/common.js"></script>
<script src="/js/deals.js"></script>
</body>
</html>
