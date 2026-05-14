const parseResponse = async (res) => {
    const contentType = res.headers.get("content-type") || "";
    const body = contentType.includes("application/json") ? await res.json() : await res.text();
    if (!res.ok || body.success === false) {
        const message = typeof body === "string" ? body || res.statusText : body.message || res.statusText;
        const error = new Error(message);
        error.status = res.status;
        error.code = typeof body === "string" ? undefined : body.code;
        throw error;
    }
    return body.data ?? body;
};

const api = (url, options = {}) => {
    const { suppressError, headers, ...fetchOptions } = options;
    return fetch(url, {
        headers: { "Content-Type": "application/json", ...(headers || {}) },
        ...fetchOptions
    }).then(parseResponse).catch((error) => {
        if (!suppressError) {
            showBrowserError(error.message);
        }
        throw error;
    });
};

const pretty = (value) => JSON.stringify(value, null, 2);

const show = (selector, value) => {
    const target = document.querySelector(selector);
    if (target) {
        target.textContent = typeof value === "string" ? value : pretty(value);
    }
};

function showBrowserError(message) {
    let box = document.querySelector("#globalError");
    if (!box) {
        box = document.createElement("div");
        box.id = "globalError";
        box.className = "global-error";
        box.setAttribute("role", "alert");
        document.body.prepend(box);
    }
    box.innerHTML = `<strong>오류</strong><span>${escapeHtml(message || "요청 처리 중 오류가 발생했습니다.")}</span><button type="button" aria-label="닫기">x</button>`;
    box.querySelector("button").addEventListener("click", () => box.remove());
    window.clearTimeout(showBrowserError.timer);
    showBrowserError.timer = window.setTimeout(() => box.remove(), 8000);
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

window.addEventListener("unhandledrejection", (event) => {
    showBrowserError(event.reason?.message || "요청 처리 중 오류가 발생했습니다.");
});

window.addEventListener("error", (event) => {
    showBrowserError(event.message || "화면 처리 중 오류가 발생했습니다.");
});

const getCookie = (name) => document.cookie
    .split("; ")
    .find((row) => row.startsWith(`${name}=`))
    ?.split("=")[1];

const setCookie = (name, value, maxAge = 60 * 60 * 24 * 365) => {
    document.cookie = `${name}=${value}; path=/; max-age=${maxAge}; SameSite=Lax`;
};

function applyTheme(theme) {
    document.documentElement.dataset.theme = theme;
    setCookie("theme", theme);
    const toggle = document.querySelector("#themeToggle");
    if (toggle) {
        toggle.textContent = theme === "dark" ? "라이트 모드" : "다크 모드";
    }
}

function authPayload(prefix) {
    return {
        email: document.querySelector(`#${prefix}Email`)?.value?.trim(),
        password: document.querySelector(`#${prefix}Password`)?.value,
        name: document.querySelector(`#${prefix}Name`)?.value?.trim(),
        phone: document.querySelector(`#${prefix}Phone`)?.value?.trim(),
        address: document.querySelector(`#${prefix}Address`)?.value?.trim()
    };
}

function renderAuthModal() {
    if (document.querySelector("#authModal")) {
        return;
    }
    const modal = document.createElement("div");
    modal.id = "authModal";
    modal.className = "modal-backdrop";
    modal.hidden = true;
    modal.innerHTML = `
        <section class="auth-modal" role="dialog" aria-modal="true" aria-labelledby="authTitle">
            <div class="panel-title">
                <div>
                    <p class="eyebrow">세션 로그인</p>
                    <h2 id="authTitle">계정으로 시작하기</h2>
                </div>
                <button type="button" class="ghost small" data-auth-close>닫기</button>
            </div>
            <div class="tab-actions" role="tablist" aria-label="로그인과 회원가입">
                <button type="button" class="small" data-auth-tab="login">로그인</button>
                <button type="button" class="ghost small" data-auth-tab="register">회원가입</button>
            </div>
            <div class="auth-pane" data-auth-pane="login">
                <label>이메일 <input id="loginEmail" type="email" value="test@ssafy.com"></label>
                <label>비밀번호 <input id="loginPassword" type="password" value="1234"></label>
                <button type="button" id="modalLogin">로그인</button>
            </div>
            <div class="auth-pane" data-auth-pane="register" hidden>
                <label>이메일 <input id="registerEmail" type="email" value="test@ssafy.com"></label>
                <label>비밀번호 <input id="registerPassword" type="password" value="1234"></label>
                <label>이름 <input id="registerName" value="김싸피"></label>
                <label>전화번호 <input id="registerPhone" value="010-0000-0000"></label>
                <label>주소 <input id="registerAddress" value="서울"></label>
                <button type="button" id="modalRegister">회원가입</button>
            </div>
            <pre id="authResult">로그인과 회원가입은 기능을 분리해 처리합니다.</pre>
        </section>
    `;
    document.body.append(modal);

    modal.querySelector("[data-auth-close]").addEventListener("click", closeAuthModal);
    modal.addEventListener("click", (event) => {
        if (event.target === modal) {
            closeAuthModal();
        }
    });
    modal.querySelectorAll("[data-auth-tab]").forEach((button) => {
        button.addEventListener("click", () => switchAuthTab(button.dataset.authTab));
    });
    modal.querySelector("#modalLogin").addEventListener("click", async () => {
        const result = await api("/api/auth/login", {
            method: "POST",
            body: JSON.stringify(authPayload("login"))
        });
        show("#authResult", result);
        await renderSession();
        closeAuthModal();
        followRedirect();
    });
    modal.querySelector("#modalRegister").addEventListener("click", async () => {
        const result = await api("/api/auth/register", {
            method: "POST",
            body: JSON.stringify(authPayload("register"))
        });
        show("#authResult", result);
        await renderSession();
        closeAuthModal();
        followRedirect();
    });
}

function switchAuthTab(tab) {
    document.querySelectorAll("[data-auth-tab]").forEach((button) => {
        button.classList.toggle("ghost", button.dataset.authTab !== tab);
    });
    document.querySelectorAll("[data-auth-pane]").forEach((pane) => {
        pane.hidden = pane.dataset.authPane !== tab;
    });
}

function openAuthModal(tab = "login") {
    renderAuthModal();
    switchAuthTab(tab);
    document.querySelector("#authModal").hidden = false;
    document.querySelector(tab === "login" ? "#loginEmail" : "#registerEmail")?.focus();
}

function closeAuthModal() {
    const modal = document.querySelector("#authModal");
    if (modal) {
        modal.hidden = true;
    }
}

function followRedirect() {
    const params = new URLSearchParams(location.search);
    const redirect = params.get("redirect");
    if (redirect && redirect.startsWith("/")) {
        location.href = redirect;
    }
}

async function renderSession() {
    const tools = document.querySelector(".header-tools");
    const target = document.querySelector("#sessionStatus");
    if (!tools || !target) {
        return;
    }
    if (!document.querySelector("#loginOpen")) {
        target.insertAdjacentHTML("afterend", `
            <button id="loginOpen" class="small">로그인</button>
            <button id="registerOpen" class="ghost small">회원가입</button>
            <button id="logoutOpen" class="ghost small" hidden>로그아웃</button>
        `);
        document.querySelector("#loginOpen").addEventListener("click", () => openAuthModal("login"));
        document.querySelector("#registerOpen").addEventListener("click", () => openAuthModal("register"));
        document.querySelector("#logoutOpen").addEventListener("click", async () => {
            await api("/api/auth/logout", { method: "POST" });
            await renderSession();
        });
    }
    try {
        const member = await api("/api/auth/me", { suppressError: true });
        target.textContent = `${member.name}님`;
        target.classList.add("signed-in");
        document.querySelector("#loginOpen").hidden = true;
        document.querySelector("#registerOpen").hidden = true;
        document.querySelector("#logoutOpen").hidden = false;
    } catch {
        target.textContent = "로그인이 필요합니다";
        target.classList.remove("signed-in");
        document.querySelector("#loginOpen").hidden = false;
        document.querySelector("#registerOpen").hidden = false;
        document.querySelector("#logoutOpen").hidden = true;
    }
}

function renderMiniMap() {
    if (document.querySelector("#miniMap")) {
        return;
    }
    const map = document.createElement("aside");
    map.id = "miniMap";
    map.className = "mini-map";
    map.innerHTML = `
        <div class="mini-map-head">
            <strong>위치 확인</strong>
            <span id="miniMapTitle">서울 중심</span>
        </div>
        <iframe id="miniMapFrame" title="선택 매물 위치 지도" loading="lazy"></iframe>
    `;
    document.body.append(map);
    updateMiniMap({ lat: 37.5665, lng: 126.9780, title: "서울 중심" });
}

function updateMiniMap({ lat, lng, title }) {
    const frame = document.querySelector("#miniMapFrame");
    const label = document.querySelector("#miniMapTitle");
    const y = Number(lat);
    const x = Number(lng);
    if (!frame || !Number.isFinite(y) || !Number.isFinite(x)) {
        return;
    }
    const delta = 0.01;
    frame.src = `https://www.openstreetmap.org/export/embed.html?bbox=${x - delta}%2C${y - delta}%2C${x + delta}%2C${y + delta}&layer=mapnik&marker=${y}%2C${x}`;
    if (label) {
        label.textContent = title || "선택 위치";
    }
}

function renderFooter() {
    if (document.querySelector(".site-footer")) {
        return;
    }
    const footer = document.createElement("footer");
    footer.className = "site-footer";
    footer.innerHTML = `
        <div>
            <strong>SSAFY Home</strong>
            <span>공공데이터 기반 주거 정보 서비스</span>
        </div>
        <a class="admin-entry" href="/admin" aria-label="관리자 페이지">관리자</a>
    `;
    document.body.append(footer);
}

window.addEventListener("property-map:update", (event) => updateMiniMap(event.detail || {}));

document.addEventListener("DOMContentLoaded", () => {
    applyTheme(getCookie("theme") || "light");
    document.querySelector("#themeToggle")?.addEventListener("click", () => {
        applyTheme(document.documentElement.dataset.theme === "dark" ? "light" : "dark");
    });
    renderAuthModal();
    renderMiniMap();
    renderFooter();
    renderSession().then(() => {
        if (new URLSearchParams(location.search).has("redirect")) {
            openAuthModal("login");
        }
    });
});
