<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>아파트 단지 | SSAFY Home</title>
    <link rel="stylesheet" href="/css/app.css">
</head>
<body>
<header class="topbar">
    <a class="brand" href="/">SSAFY Home</a>
    <nav class="nav">
        <a href="/deals">실거래가</a>
        <a class="active" href="/houses">아파트 단지</a>
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
        <h1>아파트 단지 검색</h1>
        <p>초기 DB의 법정동, 아파트 기본 정보, 거래 이력을 연결해 단지별 최근 거래와 상세 이력을 확인합니다.</p>
    </section>

    <section class="panel search-panel">
        <div class="form-grid">
            <label>지역/동 검색 <input id="dongKeyword" placeholder="예: 종로구, 무악동, 11110"></label>
            <label>법정동 코드 <input id="dongCode" placeholder="지역 검색 결과에서 선택"></label>
            <label class="wide">아파트/도로명 검색 <input id="houseKeyword" placeholder="예: 현대, 중흥, 도로명"></label>
        </div>
        <div class="actions">
            <button id="searchDongCodes" class="secondary">지역 찾기</button>
            <button id="searchHouses">단지 검색</button>
        </div>
    </section>

    <section class="split">
        <div class="panel">
            <h2>지역 검색 결과</h2>
            <div class="table-wrap">
                <table>
                    <thead><tr><th>코드</th><th>시도</th><th>구군</th><th>동</th><th>선택</th></tr></thead>
                    <tbody id="dongRows"><tr><td colspan="5">지역을 검색하세요.</td></tr></tbody>
                </table>
            </div>
        </div>
        <div class="panel">
            <h2>단지 검색 결과</h2>
            <div class="table-wrap">
                <table>
                    <thead><tr><th>단지명</th><th>지역</th><th>준공</th><th>거래수</th><th>최근거래</th><th>상세</th></tr></thead>
                    <tbody id="houseRows"><tr><td colspan="6">단지를 검색하세요.</td></tr></tbody>
                </table>
            </div>
        </div>
    </section>

    <section class="panel">
        <h2>단지 상세 및 거래 이력</h2>
        <pre id="houseDetail">상세 버튼을 누르면 단지 정보와 최근 거래 이력이 표시됩니다.</pre>
    </section>
</main>
<script src="/js/common.js"></script>
<script src="/js/houses.js"></script>
</body>
</html>
