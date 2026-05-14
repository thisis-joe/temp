<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>공지사항 | SSAFY Home</title>
    <link rel="stylesheet" href="/css/app.css">
</head>
<body>
<header class="topbar">
    <a class="brand" href="/">SSAFY Home</a>
    <nav class="nav">
        <a href="/deals">실거래가</a><a href="/houses">주거 단지</a><a href="/favorites">관심지역</a><a class="active" href="/notices">공지사항</a><a href="/regions">지역정보</a>
    </nav>
    <div class="header-tools"><span id="sessionStatus">로그인 확인 중</span><button id="themeToggle" class="ghost small">다크 모드</button></div>
</header>
<main class="page">
    <section class="page-head"><h1>공지사항</h1><p>서비스 공지를 목록 중심으로 확인하고, 필요한 경우 아래 편집 영역에서 작성합니다.</p></section>
    <section class="panel">
        <div class="panel-title">
            <h2>공지 조회</h2>
            <button id="listNotice" class="secondary">새로고침</button>
        </div>
        <label>검색어 <input id="noticeKeyword" placeholder="제목 또는 내용"></label>
        <div class="table-wrap">
            <table>
                <thead><tr><th>ID</th><th>제목</th><th>조회수</th><th>작성일</th><th>선택</th></tr></thead>
                <tbody id="noticeRows"><tr><td colspan="5">공지 목록을 불러오세요.</td></tr></tbody>
            </table>
        </div>
    </section>
    <section class="panel">
        <h2>공지 작성 및 수정</h2>
        <label>제목 <input id="noticeTitle" value="SSAFY Home 공지"></label>
        <label>내용 <textarea id="noticeContent">실거래가 검색 서비스를 시작합니다.</textarea></label>
        <label>수정/삭제 ID <input id="noticeId" placeholder="목록에서 선택하거나 공지 ID 입력"></label>
        <div class="actions"><button id="createNotice">등록</button><button id="updateNotice" class="secondary">수정</button><button id="deleteNotice" class="danger">삭제</button></div>
        <pre id="noticeResult">등록, 수정, 삭제 결과가 표시됩니다.</pre>
    </section>
</main>
<script src="/js/common.js"></script>
<script src="/js/notices.js"></script>
</body>
</html>
