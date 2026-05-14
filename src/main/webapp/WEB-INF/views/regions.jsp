<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>지역정보 | SSAFY Home</title>
    <link rel="stylesheet" href="/css/app.css">
</head>
<body>
<header class="topbar">
    <a class="brand" href="/">SSAFY Home</a>
    <nav class="nav">
        <a href="/deals">실거래가</a><a href="/houses">아파트 단지</a><a href="/members">로그인/회원</a><a href="/favorites">관심지역</a><a href="/notices">공지사항</a><a class="active" href="/regions">지역정보</a><a href="/admin">관리자</a>
    </nav>
    <div class="header-tools"><span id="sessionStatus">로그인 확인 중</span><button id="themeToggle" class="ghost small">다크 모드</button></div>
</header>
<main class="page">
    <section class="page-head">
        <h1>지역정보</h1>
        <p>서비스 화면은 DB 법정동 코드를 기준으로 동작하고, 외부 연동 상태는 VWorld와 SGIS 원문 호출로 확인합니다.</p>
    </section>
    <section class="panel">
        <div class="form-grid compact">
            <label>시도 코드 <input id="sidoCode" value="11"></label>
            <label>시군구 코드 <input id="sigunguCode" value="11110"></label>
        </div>
        <div class="actions">
            <button id="loadSido">시도 조회</button>
            <button id="loadSigungu" class="secondary">시군구 조회</button>
            <button id="loadDong" class="secondary">읍면동 조회</button>
            <button id="loadSgis" class="ghost">SGIS 토큰</button>
        </div>
        <div class="actions">
            <button id="loadVworldSido" class="ghost">VWorld 시도 원문</button>
            <button id="loadVworldSigungu" class="ghost">VWorld 시군구 원문</button>
            <button id="loadVworldDong" class="ghost">VWorld 읍면동 원문</button>
        </div>
        <pre id="regionResult">조회 버튼을 눌러 지역 정보를 확인하세요.</pre>
    </section>
</main>
<script src="/js/common.js"></script>
<script src="/js/regions.js"></script>
</body>
</html>
