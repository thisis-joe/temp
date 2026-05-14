<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>로그인/회원 | SSAFY Home</title>
    <link rel="stylesheet" href="/css/app.css">
</head>
<body>
<header class="topbar">
    <a class="brand" href="/">SSAFY Home</a>
    <nav class="nav">
        <a href="/deals">실거래가</a><a href="/houses">아파트 단지</a><a class="active" href="/members">로그인/회원</a><a href="/favorites">관심지역</a><a href="/notices">공지사항</a><a href="/regions">지역정보</a><a href="/admin">관리자</a>
    </nav>
    <div class="header-tools"><span id="sessionStatus">로그인 확인 중</span><button id="themeToggle" class="ghost small">다크 모드</button></div>
</header>
<main class="page">
    <section class="page-head"><h1>로그인과 회원 관리</h1><p>세션 기반 로그인 상태를 확인하고 회원 정보를 조회합니다.</p></section>
    <section class="split">
        <div class="panel">
            <h2>가입 및 로그인</h2>
            <label>이메일 <input id="email" value="test@ssafy.com"></label>
            <label>비밀번호 <input id="password" type="password" value="1234"></label>
            <label>이름 <input id="name" value="김싸피"></label>
            <label>전화번호 <input id="phone" value="010-0000-0000"></label>
            <label>주소 <input id="address" value="서울"></label>
            <div class="actions"><button id="register">가입</button><button id="login" class="secondary">로그인</button><button id="me" class="ghost">내 정보</button><button id="logout" class="ghost">로그아웃</button></div>
            <pre id="memberResult"></pre>
        </div>
        <div class="panel">
            <h2>회원 조회</h2>
            <label>검색어 <input id="memberKeyword" placeholder="이름 또는 이메일"></label>
            <div class="actions"><button id="listMembers">회원 목록</button></div>
            <pre id="memberListResult"></pre>
        </div>
    </section>
</main>
<script src="/js/common.js"></script>
<script src="/js/members.js"></script>
</body>
</html>
