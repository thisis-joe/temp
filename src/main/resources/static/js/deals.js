document.querySelector("#fetchDeals").addEventListener("click", async () => {
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

document.querySelector("#fetchAllDeals").addEventListener("click", async () => {
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

document.querySelector("#searchDeals").addEventListener("click", async () => {
    const params = commonDealParams();
    params.set("dealType", document.querySelector("#dealType").value);
    const houseName = document.querySelector("#houseName").value;
    if (houseName) {
        params.set("houseName", houseName);
    }
    try {
        renderDeals(await api(`/api/deals?${params}`));
    } catch (error) {
        alert(error.message);
    }
});

document.querySelector("#loadSummary").addEventListener("click", loadSummary);

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
            <td>${label(deal.dealType)}</td>
            <td>${deal.umdNm ?? ""}</td>
            <td>${deal.houseName ?? ""}</td>
            <td>${deal.dealYear ?? ""}.${deal.dealMonth ?? ""}.${deal.dealDay ?? ""}</td>
            <td>${formatNumber(deal.dealAmount ?? deal.deposit)}</td>
            <td>${formatNumber(deal.monthlyRent)}</td>
            <td>${deal.exclusiveArea ?? deal.landArea ?? ""}</td>
            <td>${deal.floor ?? ""}</td>
        </tr>
    `).join("");
    document.querySelector("#dealRows").innerHTML = rows || `<tr><td colspan="8">결과가 없습니다.</td></tr>`;
}

function renderSummary(summaries) {
    const rows = summaries.map((item) => `
        <tr>
            <td>${label(item.dealType)}</td>
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
