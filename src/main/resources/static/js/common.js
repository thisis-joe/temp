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
    box.innerHTML = `<strong>오류</strong><span>${escapeHtml(message || "요청 처리 중 오류가 발생했습니다.")}</span><button type="button" aria-label="닫기">×</button>`;
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

async function renderSession() {
    const target = document.querySelector("#sessionStatus");
    if (!target) {
        return;
    }
    try {
        const member = await api("/api/auth/me", { suppressError: true });
        target.textContent = `${member.name}님 로그인 중`;
        target.classList.add("signed-in");
    } catch {
        target.textContent = "로그인이 필요합니다";
        target.classList.remove("signed-in");
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

document.addEventListener("DOMContentLoaded", () => {
    applyTheme(getCookie("theme") || "light");
    document.querySelector("#themeToggle")?.addEventListener("click", () => {
        applyTheme(document.documentElement.dataset.theme === "dark" ? "light" : "dark");
    });
    renderFooter();
    renderSession();
});
