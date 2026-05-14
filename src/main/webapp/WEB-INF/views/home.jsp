<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>SSAFY Home | 실거래 기반 주거 정보</title>
    <link rel="stylesheet" href="/css/app.css">
</head>
<body>
<header class="topbar">
    <a class="brand" href="/">SSAFY Home</a>
    <nav class="nav">
        <a href="/deals">실거래가</a>
        <a href="/houses">주거 단지</a>
        <a href="/favorites">관심지역</a>
        <a href="/notices">공지사항</a>
        <a href="/regions">지역정보</a>
    </nav>
    <div class="header-tools">
        <span id="sessionStatus">로그인 확인 중</span>
        <button id="themeToggle" class="ghost small">다크 모드</button>
    </div>
</header>

<main class="page service-home">
    <section class="service-hero">
        <div class="hero-copy">
            <p class="eyebrow">공공데이터 기반 부동산 정보</p>
            <h1>관심 지역의 실거래 흐름과 단지 정보를 한 화면에서 확인하세요.</h1>
            <p>아파트와 연립다세대 매매·전월세 실거래, 법정동 코드, 단지별 거래 이력을 연결해 실제 의사결정에 필요한 정보를 빠르게 제공합니다.</p>
            <div class="actions">
                <a class="button-link" href="/houses">단지 검색</a>
                <a class="button-link secondary" href="/deals">실거래 조회</a>
            </div>
        </div>
        <div class="market-snapshot" aria-label="서비스 요약">
            <div>
                <span>조회 기준</span>
                <strong>지역 · 단지 · 기간</strong>
            </div>
            <div>
                <span>제공 정보</span>
                <strong>매매 · 전월세 · 시세 요약</strong>
            </div>
            <div>
                <span>개인화</span>
                <strong>관심지역 저장</strong>
            </div>
        </div>
    </section>

    <section class="quick-search panel">
        <div>
            <h2>바로 시작하기</h2>
            <p>단지명을 알고 있다면 단지 검색부터, 지역과 월을 알고 있다면 실거래 조회부터 시작하세요.</p>
        </div>
        <div class="quick-actions">
            <a href="/houses">아파트·연립다세대 찾기</a>
            <a href="/deals">월별 실거래 분석</a>
            <a href="/favorites">관심지역 보기</a>
        </div>
    </section>

    <section class="service-grid">
        <a class="feature-link" href="/houses">
            <strong>주거 단지 검색</strong>
            <span>DB에 저장된 아파트 단지와 연립다세대 실거래 흐름을 함께 확인합니다.</span>
        </a>
        <a class="feature-link" href="/deals">
            <strong>실거래 수집 및 분석</strong>
            <span>공공 API로 거래 데이터를 수집하고 유형별 월간 시세를 요약합니다.</span>
        </a>
        <a class="feature-link" href="/regions">
            <strong>지역 코드 확인</strong>
            <span>법정동 코드와 외부 지역 API 응답을 비교하며 조회합니다.</span>
        </a>
        <a class="feature-link" href="/notices">
            <strong>서비스 공지</strong>
            <span>데이터 업데이트, 기능 변경, 점검 안내를 확인합니다.</span>
        </a>
    </section>
</main>
<script src="/js/common.js"></script>
</body>
</html>
