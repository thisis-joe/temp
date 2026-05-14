<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>계정 | SSAFY Home</title>
    <link rel="stylesheet" href="/css/app.css">
</head>
<body>
<header class="topbar">
    <a class="brand" href="/">SSAFY Home</a>
    <nav class="nav">
        <a href="/deals">실거래가</a><a href="/houses">주거 단지</a><a href="/favorites">관심지역</a><a href="/notices">공지사항</a><a href="/regions">지역정보</a>
    </nav>
    <div class="header-tools"><span id="sessionStatus">로그인 확인 중</span><button id="themeToggle" class="ghost small">다크 모드</button></div>
</header>
<main class="page">
    <section class="page-head">
        <h1>계정</h1>
        <p>로그인과 회원가입은 우측 상단 버튼에서 분리해 처리합니다. 회원 목록 조회는 관리자 대시보드에서만 확인합니다.</p>
    </section>
    <section class="panel account-panel">
        <h2>내 계정</h2>
        <div class="actions">
            <button id="accountLogin">로그인</button>
            <button id="accountRegister" class="secondary">회원가입</button>
            <button id="me" class="ghost">내 정보 확인</button>
            <button id="logout" class="ghost">로그아웃</button>
        </div>
        <pre id="memberResult">로그인이 필요한 기능에서 이 화면으로 이동하면 로그인 창이 자동으로 열립니다.</pre>
    </section>
</main>
<script src="/js/common.js"></script>
<script src="/js/members.js"></script>
</body>
</html>
