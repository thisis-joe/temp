document.querySelector("#createNotice").addEventListener("click", async () => {
    try {
        show("#noticeResult", await api("/api/notices", {
            method: "POST",
            body: JSON.stringify(noticePayload())
        }));
    } catch (error) {
        show("#noticeResult", error.message);
    }
});

document.querySelector("#updateNotice").addEventListener("click", async () => {
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
    } catch (error) {
        show("#noticeResult", error.message);
    }
});

document.querySelector("#deleteNotice").addEventListener("click", async () => {
    const id = document.querySelector("#noticeId").value;
    if (!id) {
        show("#noticeResult", "삭제할 공지 ID를 입력하세요.");
        return;
    }
    try {
        show("#noticeResult", await api(`/api/notices/${id}`, { method: "DELETE" }));
    } catch (error) {
        show("#noticeResult", error.message);
    }
});

document.querySelector("#listNotice").addEventListener("click", async () => {
    const params = new URLSearchParams();
    const keyword = document.querySelector("#noticeKeyword").value;
    if (keyword) {
        params.set("keyword", keyword);
    }
    try {
        show("#noticeListResult", await api(`/api/notices?${params}`));
    } catch (error) {
        show("#noticeListResult", error.message);
    }
});

function noticePayload() {
    return {
        title: document.querySelector("#noticeTitle").value,
        content: document.querySelector("#noticeContent").value
    };
}
