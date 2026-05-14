const tableLabels = {
    members: "회원",
    property_deals: "수집 실거래",
    houseinfos: "주거 단지",
    housedeals: "단지 거래",
    dongcodes: "법정동 코드",
    favorites: "관심지역",
    notices: "공지사항"
};

document.querySelector("#refreshAdmin")?.addEventListener("click", loadOverview);
document.querySelector("#loadLogs")?.addEventListener("click", loadLogs);
document.querySelector("#loadTableRows")?.addEventListener("click", loadTableRows);
document.querySelector("#adminTable")?.addEventListener("change", loadTableRows);

async function loadOverview() {
    const overview = await api("/api/admin/overview");
    renderMetrics(overview);
    renderDealTypes(overview.dealTypes || []);
    renderRecentDeals(overview.recentDeals || []);
    renderLogs(overview.recentLogs || []);
}

async function loadTables() {
    const tables = await api("/api/admin/tables");
    const select = document.querySelector("#adminTable");
    select.innerHTML = tables
        .map((table) => `<option value="${table.key}">${escapeHtml(table.label)}</option>`)
        .join("");
    if ([...select.options].some((option) => option.value === "members")) {
        select.value = "members";
    }
}

async function loadLogs() {
    const category = document.querySelector("#logCategory").value;
    const params = new URLSearchParams();
    if (category) {
        params.set("category", category);
    }
    const logs = await api(`/api/logs?${params}`);
    renderLogs(logs);
}

async function loadTableRows() {
    const params = new URLSearchParams({
        table: document.querySelector("#adminTable").value,
        size: document.querySelector("#adminSize").value
    });
    const keyword = document.querySelector("#adminKeyword").value.trim();
    if (keyword) {
        params.set("keyword", keyword);
    }
    const result = await api(`/api/admin/rows?${params}`);
    renderTable(result);
}

function renderMetrics(overview) {
    const counts = overview.counts || {};
    document.querySelector("#adminMetrics").innerHTML = [
        metric("회원", counts.members, "가입 계정"),
        metric("수집 실거래", counts.property_deals, `최근 거래월 ${overview.latestDealMonth || "-"}`),
        metric("주거 단지", counts.houseinfos, `거래 ${formatNumber(counts.housedeals)}건`),
        metric("관심지역", counts.favorites, `공지 ${formatNumber(counts.notices)}건`)
    ].join("");
}

function metric(label, value, caption) {
    return `<div class="metric-card"><span>${label}</span><strong>${formatNumber(value)}</strong><small>${escapeHtml(caption || "")}</small></div>`;
}

function renderDealTypes(rows) {
    const target = document.querySelector("#dealTypeBreakdown");
    if (!rows.length) {
        target.innerHTML = `<p class="empty">수집된 실거래 데이터가 없습니다.</p>`;
        return;
    }
    const max = Math.max(...rows.map((row) => Number(row.deal_count || 0)), 1);
    target.innerHTML = rows.map((row) => {
        const count = Number(row.deal_count || 0);
        const width = Math.max(4, Math.round((count / max) * 100));
        return `
            <div class="breakdown-row">
                <div><strong>${escapeHtml(row.deal_type)}</strong><span>${formatNumber(count)}건</span></div>
                <i style="width:${width}%"></i>
            </div>
        `;
    }).join("");
}

function renderLogs(logs) {
    const target = document.querySelector("#adminLogResult");
    if (!logs.length) {
        target.innerHTML = `<p class="empty">표시할 작업 로그가 없습니다.</p>`;
        return;
    }
    target.innerHTML = logs.map((log) => `
        <article class="log-item">
            <span>${escapeHtml(log.time || log.createdAt || "-")}</span>
            <strong>${escapeHtml(log.category)} · ${escapeHtml(log.action)}</strong>
            <p>${escapeHtml(log.message)}</p>
        </article>
    `).join("");
}

function renderRecentDeals(rows) {
    const target = document.querySelector("#recentDealRows");
    if (!rows.length) {
        target.innerHTML = `<tr><td colspan="8">최근 수집 실거래가 없습니다.</td></tr>`;
        return;
    }
    target.innerHTML = rows.map((row) => `
        <tr>
            <td>${escapeHtml(row.id)}</td>
            <td>${escapeHtml(row.deal_type)}</td>
            <td>${escapeHtml(row.lawd_cd)}</td>
            <td>${escapeHtml(row.umd_nm || "-")}</td>
            <td>${escapeHtml(row.house_name || "-")}</td>
            <td>${escapeHtml(`${row.deal_year || "-"}-${String(row.deal_month || "-").padStart(2, "0")}`)}</td>
            <td>${escapeHtml(formatMoney(row.deal_amount || row.deposit || "-"))}</td>
            <td>${escapeHtml(row.created_at || "-")}</td>
        </tr>
    `).join("");
}

function renderTable(result) {
    document.querySelector("#tableSummary").textContent = `${result.label} 총 ${formatNumber(result.total)}건 중 최대 ${result.rows.length}건 표시`;
    document.querySelector("#adminTableHead").innerHTML = `<tr>${result.columns.map((column) => `<th>${escapeHtml(column)}</th>`).join("")}</tr>`;
    const body = document.querySelector("#adminTableRows");
    if (!result.rows.length) {
        body.innerHTML = `<tr><td colspan="${result.columns.length}">조건에 맞는 데이터가 없습니다.</td></tr>`;
        return;
    }
    body.innerHTML = result.rows.map((row) => `
        <tr>${result.columns.map((column) => `<td>${escapeHtml(row[column] ?? "-")}</td>`).join("")}</tr>
    `).join("");
}

function formatNumber(value) {
    return Number(value || 0).toLocaleString("ko-KR");
}

function formatMoney(value) {
    if (value === "-") {
        return "-";
    }
    return `${formatNumber(value)}만원`;
}

(async function initAdmin() {
    await loadTables();
    await loadOverview();
    await loadTableRows();
})();
