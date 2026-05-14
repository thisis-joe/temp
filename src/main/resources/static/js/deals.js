document.querySelector("#fetchDeals")?.addEventListener("click", async () => {
    const params = commonDealParams();
    params.set("type", document.querySelector("#dealType").value);
    params.set("numOfRows", "100");
    try {
        renderDeals(await api(`/api/deals/fetch?${params}`, { method: "POST" }));
        await loadSummary();
    } catch (error) {
        alert(error.message);
    }
});

document.querySelector("#fetchAllDeals")?.addEventListener("click", async () => {
    const params = commonDealParams();
    params.set("numOfRows", "100");
    try {
        const result = await api(`/api/deals/fetch-all?${params}`, { method: "POST" });
        await loadSummary();
        alert(result.map((item) => `${label(item.dealType)} ${item.savedCount}건`).join("\n"));
    } catch (error) {
        alert(error.message);
    }
});

document.querySelector("#searchDeals")?.addEventListener("click", searchDeals);
document.querySelector("#dealKeyword")?.addEventListener("keydown", (event) => {
    if (event.key === "Enter") {
        searchDeals();
    }
});
document.querySelector("#loadSummary")?.addEventListener("click", loadSummary);

document.addEventListener("DOMContentLoaded", () => {
    const params = new URLSearchParams(location.search);
    const lawdCd = params.get("lawdCd");
    if (lawdCd && document.querySelector("#lawdCd")) {
        document.querySelector("#lawdCd").value = lawdCd;
        document.querySelector("#dealKeyword").value = lawdCd;
        searchDeals();
    }
});

async function searchDeals() {
    const params = new URLSearchParams();
    const keyword = document.querySelector("#dealKeyword")?.value?.trim();
    const lawdCd = document.querySelector("#lawdCd")?.value?.trim();
    const dealYmd = document.querySelector("#dealYmd")?.value?.trim();
    if (keyword) {
        params.set("keyword", keyword);
    }
    if (lawdCd && !keyword) {
        params.set("lawdCd", lawdCd);
    }
    if (dealYmd) {
        params.set("dealYmd", dealYmd);
    }
    try {
        const deals = dedupe(await api(`/api/deals?${params}`), dealKey);
        renderDeals(deals);
    } catch (error) {
        alert(error.message);
    }
}

async function loadSummary() {
    const params = commonDealParams();
    const summaries = await api(`/api/deals/summary?${params}`);
    renderSummary(summaries);
}

function commonDealParams() {
    return new URLSearchParams({
        lawdCd: document.querySelector("#lawdCd").value,
        dealYmd: document.querySelector("#dealYmd").value
    });
}

function renderDeals(deals) {
    const rows = deals.map((deal) => `
        <tr>
            <td>${escapeHtml(label(deal.dealType))}</td>
            <td>${escapeHtml(deal.umdNm ?? "")}</td>
            <td>${escapeHtml(deal.houseName ?? "")}</td>
            <td>${deal.dealYear ?? ""}.${deal.dealMonth ?? ""}.${deal.dealDay ?? ""}</td>
            <td>${formatNumber(deal.dealAmount ?? deal.deposit)}</td>
            <td>${formatNumber(deal.monthlyRent)}</td>
            <td>${deal.exclusiveArea ?? deal.landArea ?? ""}</td>
            <td>${escapeHtml(deal.floor ?? "")}</td>
        </tr>
    `).join("");
    document.querySelector("#dealRows").innerHTML = rows || `<tr><td colspan="8">결과가 없습니다.</td></tr>`;
}

function renderSummary(summaries) {
    const rows = summaries.map((item) => `
        <tr>
            <td>${escapeHtml(label(item.dealType))}</td>
            <td>${formatNumber(item.dealCount)}</td>
            <td>${formatNumber(item.minDealAmount)}</td>
            <td>${formatNumber(item.avgDealAmount)}</td>
            <td>${formatNumber(item.maxDealAmount)}</td>
            <td>${formatNumber(item.avgDeposit)}</td>
            <td>${formatNumber(item.avgMonthlyRent)}</td>
            <td>${item.avgExclusiveArea ?? ""}</td>
            <td>${item.avgPricePerSquareMeter ?? ""}</td>
        </tr>
    `).join("");
    document.querySelector("#summaryRows").innerHTML = rows || `<tr><td colspan="9">요약할 거래가 없습니다.</td></tr>`;
}

function dealKey(deal) {
    return [
        deal.dealType, deal.lawdCd, deal.umdNm, deal.houseName, deal.jibun,
        deal.dealYear, deal.dealMonth, deal.dealDay, deal.dealAmount,
        deal.deposit, deal.monthlyRent, deal.exclusiveArea, deal.landArea, deal.floor
    ].join("|");
}

function label(type) {
    return {
        APT_TRADE: "아파트 매매",
        APT_RENT: "아파트 전월세",
        RH_TRADE: "연립다세대 매매",
        RH_RENT: "연립다세대 전월세"
    }[type] ?? type ?? "";
}

function formatNumber(value) {
    return value == null ? "" : Number(value).toLocaleString("ko-KR");
}

function dedupe(items, keySelector) {
    return Array.from(new Map(items.map((item) => [keySelector(item), item])).values());
}
