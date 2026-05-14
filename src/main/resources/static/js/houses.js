document.querySelector("#searchDongCodes")?.addEventListener("click", searchDongCodes);
document.querySelector("#searchHouses")?.addEventListener("click", searchHouses);
document.querySelector("#houseKeyword")?.addEventListener("keydown", (event) => {
    if (event.key === "Enter") {
        searchHouses();
    }
});

async function searchDongCodes() {
    const params = new URLSearchParams();
    const keyword = document.querySelector("#houseKeyword")?.value?.trim();
    if (keyword) {
        params.set("keyword", keyword);
    }
    try {
        renderDongCodes(await api(`/api/houses/dongcodes?${params}`));
    } catch (error) {
        show("#houseDetail", error.message);
    }
}

async function searchHouses() {
    const params = new URLSearchParams();
    const keyword = document.querySelector("#houseKeyword")?.value?.trim();
    const dongCode = document.querySelector("#dongCode")?.value?.trim();
    if (keyword) {
        params.set("keyword", keyword);
        if (/^\d{5,10}$/.test(keyword) && !dongCode) {
            params.set("dongCode", keyword.length > 10 ? keyword.substring(0, 10) : keyword);
        }
    }
    if (dongCode) {
        params.set("dongCode", dongCode);
    }
    try {
        const houses = dedupe(await api(`/api/houses?${params}`), (house) => house.aptSeq || `${house.aptNm}-${house.umdNm}-${house.jibun}`);
        renderHouses(houses);
    } catch (error) {
        show("#houseDetail", error.message);
    }
}

function renderDongCodes(dongs) {
    const rows = dongs.map((dong) => `
        <tr>
            <td>${escapeHtml(dong.dongCode ?? "")}</td>
            <td>${escapeHtml(dong.sidoName ?? "")}</td>
            <td>${escapeHtml(dong.gugunName ?? "")}</td>
            <td>${escapeHtml(dong.dongName ?? "")}</td>
            <td><button class="small ghost" data-dong-code="${escapeHtml(dong.dongCode)}">선택</button></td>
        </tr>
    `).join("");
    document.querySelector("#dongRows").innerHTML = rows || `<tr><td colspan="5">검색된 지역이 없습니다.</td></tr>`;
    document.querySelectorAll("[data-dong-code]").forEach((button) => {
        button.addEventListener("click", () => {
            document.querySelector("#dongCode").value = button.dataset.dongCode;
            searchHouses();
        });
    });
}

function renderHouses(houses) {
    const rows = houses.map((house) => `
        <tr>
            <td>${escapeHtml(house.aptNm ?? "")}</td>
            <td>${escapeHtml([house.sidoName, house.gugunName, house.dongName ?? house.umdNm].filter(Boolean).join(" "))}</td>
            <td>${house.buildYear ?? ""}</td>
            <td>${house.dealCount ?? 0}</td>
            <td>${escapeHtml(formatLatestDeal(house))}</td>
            <td><button class="small ghost" data-apt-seq="${escapeHtml(house.aptSeq)}">상세</button></td>
        </tr>
    `).join("");
    document.querySelector("#houseRows").innerHTML = rows || `<tr><td colspan="6">검색된 단지가 없습니다. 덤프 데이터가 import 되었는지 확인하세요.</td></tr>`;
    document.querySelectorAll("[data-apt-seq]").forEach((button) => {
        button.addEventListener("click", () => loadDetail(button.dataset.aptSeq));
    });
}

async function loadDetail(aptSeq) {
    try {
        const detail = await api(`/api/houses/${encodeURIComponent(aptSeq)}`);
        renderDetail(detail);
        const house = detail.house;
        window.dispatchEvent(new CustomEvent("property-map:update", {
            detail: {
                lat: house.latitude,
                lng: house.longitude,
                title: `${house.aptNm ?? "선택 단지"} ${house.umdNm ?? ""}`
            }
        }));
    } catch (error) {
        show("#houseDetail", error.message);
    }
}

function renderDetail(detail) {
    const house = detail.house;
    const deals = detail.deals || [];
    const rows = deals.slice(0, 20).map((deal) =>
        `${deal.dealYear}.${deal.dealMonth}.${deal.dealDay} ${deal.floor ?? ""}층 ${deal.excluUseAr ?? ""}㎡ ${deal.dealAmount ?? ""}`
    ).join("\n");
    show("#houseDetail", [
        `[단지] ${house.aptNm}`,
        `[주소] ${[house.sidoName, house.gugunName, house.umdNm, house.jibun].filter(Boolean).join(" ")}`,
        `[좌표] ${house.latitude ?? "-"}, ${house.longitude ?? "-"}`,
        `[최근 거래]`,
        rows || "거래 이력이 없습니다."
    ].join("\n"));
}

function formatLatestDeal(house) {
    if (!house.latestDealYear) {
        return "";
    }
    return `${house.latestDealYear}.${house.latestDealMonth ?? ""} ${house.latestDealAmount ?? ""}`;
}

function dedupe(items, keySelector) {
    return Array.from(new Map(items.map((item) => [keySelector(item), item])).values());
}
