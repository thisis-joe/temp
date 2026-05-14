document.querySelector("#searchDongCodes").addEventListener("click", searchDongCodes);
document.querySelector("#searchHouses").addEventListener("click", searchHouses);

async function searchDongCodes() {
    const params = new URLSearchParams();
    const keyword = document.querySelector("#dongKeyword").value;
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
    const keyword = document.querySelector("#houseKeyword").value;
    const dongCode = document.querySelector("#dongCode").value;
    if (keyword) {
        params.set("keyword", keyword);
    }
    if (dongCode) {
        params.set("dongCode", dongCode);
    }
    try {
        renderHouses(await api(`/api/houses?${params}`));
    } catch (error) {
        show("#houseDetail", error.message);
    }
}

function renderDongCodes(dongs) {
    const rows = dongs.map((dong) => `
        <tr>
            <td>${dong.dongCode ?? ""}</td>
            <td>${dong.sidoName ?? ""}</td>
            <td>${dong.gugunName ?? ""}</td>
            <td>${dong.dongName ?? ""}</td>
            <td><button class="small ghost" data-dong-code="${dong.dongCode}">선택</button></td>
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
            <td>${house.aptNm ?? ""}</td>
            <td>${[house.sidoName, house.gugunName, house.dongName ?? house.umdNm].filter(Boolean).join(" ")}</td>
            <td>${house.buildYear ?? ""}</td>
            <td>${house.dealCount ?? 0}</td>
            <td>${formatLatestDeal(house)}</td>
            <td><button class="small ghost" data-apt-seq="${house.aptSeq}">상세</button></td>
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
        show("#houseDetail", detail);
    } catch (error) {
        show("#houseDetail", error.message);
    }
}

function formatLatestDeal(house) {
    if (!house.latestDealYear) {
        return "";
    }
    return `${house.latestDealYear}.${house.latestDealMonth ?? ""} ${house.latestDealAmount ?? ""}`;
}
