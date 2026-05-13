// fetch 응답을 공통 포맷(ApiResponse)으로 해석한다.
const json = (res) => res.json().then((body) => {
    if (!res.ok || body.success === false) {
        throw new Error(body.message || res.statusText);
    }
    return body.data ?? body;
});

// 모든 AJAX 요청은 이 함수를 거치게 해서 헤더와 에러 처리를 한곳에서 관리한다.
const api = (url, options = {}) => fetch(url, {
    headers: { "Content-Type": "application/json", ...(options.headers || {}) },
    ...options
}).then(json);

const pretty = (value) => JSON.stringify(value, null, 2);

document.querySelector("#fetchDeals").addEventListener("click", async () => {
    const params = new URLSearchParams({
        type: document.querySelector("#dealType").value,
        lawdCd: document.querySelector("#lawdCd").value,
        dealYmd: document.querySelector("#dealYmd").value,
        numOfRows: "50"
    });
    try {
        const deals = await api(`/api/deals/fetch?${params}`, { method: "POST" });
        renderDeals(deals);
    } catch (error) {
        alert(error.message);
    }
});

document.querySelector("#searchDeals").addEventListener("click", async () => {
    const keyword = document.querySelector("#keyword").value;
    const params = new URLSearchParams({
        dealType: document.querySelector("#dealType").value,
        lawdCd: document.querySelector("#lawdCd").value,
        dealYmd: document.querySelector("#dealYmd").value
    });
    if (keyword) {
        params.set("houseName", keyword);
    }
    try {
        const deals = await api(`/api/deals?${params}`);
        renderDeals(deals);
    } catch (error) {
        alert(error.message);
    }
});

// 서버에서 받은 실거래가 목록을 테이블 행으로 변환한다.
function renderDeals(deals) {
    document.querySelector("#dealRows").innerHTML = deals.map((deal) => `
        <tr>
            <td>${deal.dealType ?? ""}</td>
            <td>${deal.umdNm ?? ""}</td>
            <td>${deal.houseName ?? ""}</td>
            <td>${deal.dealYear ?? ""}.${deal.dealMonth ?? ""}.${deal.dealDay ?? ""}</td>
            <td>${deal.dealAmount ?? deal.deposit ?? ""}</td>
            <td>${deal.monthlyRent ?? ""}</td>
            <td>${deal.exclusiveArea ?? ""}</td>
            <td>${deal.floor ?? ""}</td>
        </tr>
    `).join("");
}

document.querySelector("#register").addEventListener("click", async () => {
    const data = await api("/api/auth/register", {
        method: "POST",
        body: JSON.stringify(memberPayload())
    });
    document.querySelector("#memberResult").textContent = pretty(data);
});

document.querySelector("#login").addEventListener("click", async () => {
    const data = await api("/api/auth/login", {
        method: "POST",
        body: JSON.stringify({
            email: document.querySelector("#email").value,
            password: document.querySelector("#password").value
        })
    });
    document.querySelector("#memberResult").textContent = pretty(data);
});

document.querySelector("#me").addEventListener("click", async () => {
    document.querySelector("#memberResult").textContent = pretty(await api("/api/auth/me"));
});

document.querySelector("#logout").addEventListener("click", async () => {
    document.querySelector("#memberResult").textContent = pretty(await api("/api/auth/logout", { method: "POST" }));
});

document.querySelector("#addFavorite").addEventListener("click", async () => {
    const data = await api("/api/favorites", {
        method: "POST",
        body: JSON.stringify({
            dongNm: document.querySelector("#favDong").value,
            lawdCd: document.querySelector("#favLawdCd").value
        })
    });
    document.querySelector("#favoriteResult").textContent = pretty(data);
});

document.querySelector("#listFavorite").addEventListener("click", async () => {
    document.querySelector("#favoriteResult").textContent = pretty(await api("/api/favorites"));
});

document.querySelector("#createNotice").addEventListener("click", async () => {
    const data = await api("/api/notices", {
        method: "POST",
        body: JSON.stringify({
            title: document.querySelector("#noticeTitle").value,
            content: document.querySelector("#noticeContent").value
        })
    });
    document.querySelector("#noticeResult").textContent = pretty(data);
});

document.querySelector("#listNotice").addEventListener("click", async () => {
    document.querySelector("#noticeResult").textContent = pretty(await api("/api/notices"));
});

function memberPayload() {
    return {
        email: document.querySelector("#email").value,
        password: document.querySelector("#password").value,
        name: document.querySelector("#name").value
    };
}
