<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>운영 대시보드 | SSAFY Home</title>
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

<main class="page admin-page">
    <section class="page-head admin-head">
        <div>
            <p class="eyebrow">운영자 전용</p>
            <h1>서비스 데이터와 작업 로그를 한눈에 확인합니다.</h1>
            <p>현재 DB 적재 상태, 최근 수집 데이터, 기능별 작업 로그를 필터링해서 점검할 수 있습니다.</p>
        </div>
        <button id="refreshAdmin" class="secondary">새로고침</button>
    </section>

    <section class="metric-grid" id="adminMetrics">
        <div class="metric-card"><span>회원</span><strong>-</strong></div>
        <div class="metric-card"><span>수집 실거래</span><strong>-</strong></div>
        <div class="metric-card"><span>아파트 단지</span><strong>-</strong></div>
        <div class="metric-card"><span>최근 거래월</span><strong>-</strong></div>
    </section>

    <section class="dashboard-grid">
        <div class="panel">
            <div class="panel-title">
                <h2>거래 유형별 적재 현황</h2>
                <span>property_deals</span>
            </div>
            <div id="dealTypeBreakdown" class="breakdown-list"></div>
        </div>
        <div class="panel">
            <div class="panel-title">
                <h2>최근 작업 로그</h2>
                <button id="loadLogs" class="ghost small">로그 조회</button>
            </div>
            <div class="form-grid compact admin-filters">
                <label>기능
                    <select id="logCategory">
                        <option value="">전체</option>
                        <option value="deals">실거래</option>
                        <option value="members">회원</option>
                        <option value="favorites">관심지역</option>
                        <option value="notices">공지사항</option>
                    </select>
                </label>
            </div>
            <div id="adminLogResult" class="log-list">로그를 불러오는 중입니다.</div>
        </div>
    </section>

    <section class="panel">
        <div class="panel-title">
            <div>
                <h2>현재 DB 조회</h2>
                <p>운영 중인 핵심 테이블을 선택하고 키워드로 필터링합니다.</p>
            </div>
            <button id="loadTableRows" class="secondary">조회</button>
        </div>
        <div class="form-grid compact admin-filters">
            <label>테이블
                <select id="adminTable"></select>
            </label>
            <label>검색어
                <input id="adminKeyword" placeholder="단지명, 지역명, 이메일, 거래유형 등">
            </label>
            <label>표시 개수
                <select id="adminSize">
                    <option>20</option>
                    <option>50</option>
                    <option>100</option>
                </select>
            </label>
        </div>
        <div id="tableSummary" class="table-summary"></div>
        <div class="table-wrap admin-table-wrap">
            <table>
                <thead id="adminTableHead"></thead>
                <tbody id="adminTableRows">
                <tr><td>테이블을 선택하고 조회하세요.</td></tr>
                </tbody>
            </table>
        </div>
    </section>

    <section class="panel">
        <div class="panel-title">
            <h2>최근 수집 실거래</h2>
            <span>최신 8건</span>
        </div>
        <div class="table-wrap">
            <table>
                <thead>
                <tr><th>ID</th><th>유형</th><th>법정동</th><th>동</th><th>단지/주택명</th><th>계약월</th><th>금액</th><th>등록일</th></tr>
                </thead>
                <tbody id="recentDealRows">
                <tr><td colspan="8">최근 거래를 불러오는 중입니다.</td></tr>
                </tbody>
            </table>
        </div>
    </section>
</main>
<script src="/js/common.js"></script>
<script src="/js/admin.js"></script>
</body>
</html>
