<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>관심지역 | SSAFY Home</title>
    <link rel="stylesheet" href="/css/app.css">
</head>
<body>
<header class="topbar">
    <a class="brand" href="/">SSAFY Home</a>
    <nav class="nav">
        <a href="/deals">실거래가</a><a href="/houses">주거 단지</a><a class="active" href="/favorites">관심지역</a><a href="/notices">공지사항</a><a href="/regions">지역정보</a>
    </nav>
    <div class="header-tools"><span id="sessionStatus">로그인 확인 중</span><button id="themeToggle" class="ghost small">다크 모드</button></div>
</header>
<main class="page">
    <section class="page-head"><h1>관심지역</h1><p>로그인한 회원의 관심 지역을 저장하고 다시 확인합니다.</p></section>
    <section class="panel">
        <div class="form-grid">
            <label>시도명 <input id="sidoNm" value="서울특별시"></label>
            <label>시군구명 <input id="sigunguNm" value="종로구"></label>
            <label>동명 <input id="dongNm" value="청운효자동"></label>
            <label>법정동 코드 <input id="favLawdCd" value="11110"></label>
            <label class="wide">메모 <input id="memo" value="출퇴근 관심 지역"></label>
        </div>
        <div class="actions"><button id="addFavorite">등록</button><button id="listFavorite" class="secondary">목록 새로고침</button></div>
        <div class="table-wrap">
            <table>
                <thead><tr><th>지역</th><th>법정동 코드</th><th>메모</th><th>관리</th></tr></thead>
                <tbody id="favoriteRows"><tr><td colspan="4">관심지역 목록을 불러오세요.</td></tr></tbody>
            </table>
        </div>
        <pre id="favoriteResult">등록 결과와 오류가 표시됩니다.</pre>
    </section>
</main>
<script src="/js/common.js"></script>
<script src="/js/favorites.js"></script>
</body>
</html>
