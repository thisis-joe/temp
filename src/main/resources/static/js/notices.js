document.querySelector("#createNotice")?.addEventListener("click", async () => {
    try {
        show("#noticeResult", await api("/api/notices", {
            method: "POST",
            body: JSON.stringify(noticePayload())
        }));
        await listNotices();
    } catch (error) {
        show("#noticeResult", error.message);
    }
});

document.querySelector("#updateNotice")?.addEventListener("click", async () => {
    const id = document.querySelector("#noticeId").value;
    if (!id) {
        show("#noticeResult", "수정할 공지 ID를 입력하세요.");
        return;
    }
    try {
        show("#noticeResult", await api(`/api/notices/${id}`, {
            method: "PUT",
            body: JSON.stringify(noticePayload())
        }));
        await listNotices();
    } catch (error) {
        show("#noticeResult", error.message);
    }
});

document.querySelector("#deleteNotice")?.addEventListener("click", async () => {
    const id = document.querySelector("#noticeId").value;
    if (!id) {
        show("#noticeResult", "삭제할 공지 ID를 입력하세요.");
        return;
    }
    try {
        show("#noticeResult", await api(`/api/notices/${id}`, { method: "DELETE" }));
        document.querySelector("#noticeId").value = "";
        await listNotices();
    } catch (error) {
        show("#noticeResult", error.message);
    }
});

document.querySelector("#listNotice")?.addEventListener("click", listNotices);
document.querySelector("#noticeKeyword")?.addEventListener("keydown", (event) => {
    if (event.key === "Enter") {
        listNotices();
    }
});

async function listNotices() {
    const params = new URLSearchParams();
    const keyword = document.querySelector("#noticeKeyword").value.trim();
    if (keyword) {
        params.set("keyword", keyword);
    }
    try {
        renderNotices(await api(`/api/notices?${params}`));
    } catch (error) {
        show("#noticeResult", error.message);
    }
}

function renderNotices(notices) {
    const rows = notices.map((notice) => `
        <tr>
            <td>${notice.id}</td>
            <td>${escapeHtml(notice.title)}</td>
            <td>${notice.viewCount ?? 0}</td>
            <td>${escapeHtml(notice.createdAt ?? "")}</td>
            <td><button class="small ghost" data-notice-id="${notice.id}">선택</button></td>
        </tr>
    `).join("");
    document.querySelector("#noticeRows").innerHTML = rows || `<tr><td colspan="5">공지사항이 없습니다.</td></tr>`;
    document.querySelectorAll("[data-notice-id]").forEach((button) => {
        button.addEventListener("click", () => loadNotice(button.dataset.noticeId));
    });
}

async function loadNotice(id) {
    const notice = await api(`/api/notices/${id}`);
    document.querySelector("#noticeId").value = notice.id;
    document.querySelector("#noticeTitle").value = notice.title ?? "";
    document.querySelector("#noticeContent").value = notice.content ?? "";
    show("#noticeResult", notice);
}

function noticePayload() {
    return {
        title: document.querySelector("#noticeTitle").value,
        content: document.querySelector("#noticeContent").value
    };
}

document.addEventListener("DOMContentLoaded", listNotices);
