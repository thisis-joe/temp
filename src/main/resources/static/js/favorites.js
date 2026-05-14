document.querySelector("#addFavorite")?.addEventListener("click", async () => {
    try {
        const favorite = await api("/api/favorites", {
            method: "POST",
            body: JSON.stringify({
                sidoNm: document.querySelector("#sidoNm").value,
                sigunguNm: document.querySelector("#sigunguNm").value,
                dongNm: document.querySelector("#dongNm").value,
                lawdCd: document.querySelector("#favLawdCd").value,
                memo: document.querySelector("#memo").value
            })
        });
        show("#favoriteResult", favorite);
        await listFavorites();
    } catch (error) {
        show("#favoriteResult", error.message);
    }
});

document.querySelector("#listFavorite")?.addEventListener("click", listFavorites);

async function listFavorites() {
    try {
        renderFavorites(await api("/api/favorites"));
    } catch (error) {
        show("#favoriteResult", error.message);
        if (error.code === "LOGIN_REQUIRED" || error.status === 401) {
            openAuthModal("login");
        }
    }
}

function renderFavorites(favorites) {
    const rows = favorites.map((favorite) => `
        <tr>
            <td>${escapeHtml([favorite.sidoNm, favorite.sigunguNm, favorite.dongNm].filter(Boolean).join(" "))}</td>
            <td>${escapeHtml(favorite.lawdCd ?? "")}</td>
            <td>${escapeHtml(favorite.memo ?? "")}</td>
            <td>
                <button class="small ghost" data-favorite-search="${escapeHtml(favorite.lawdCd)}">거래 보기</button>
                <button class="small danger" data-favorite-delete="${favorite.id}">삭제</button>
            </td>
        </tr>
    `).join("");
    document.querySelector("#favoriteRows").innerHTML = rows || `<tr><td colspan="4">저장된 관심지역이 없습니다.</td></tr>`;
    document.querySelectorAll("[data-favorite-delete]").forEach((button) => {
        button.addEventListener("click", () => deleteFavorite(button.dataset.favoriteDelete));
    });
    document.querySelectorAll("[data-favorite-search]").forEach((button) => {
        button.addEventListener("click", () => {
            location.href = `/deals?lawdCd=${encodeURIComponent(button.dataset.favoriteSearch)}`;
        });
    });
}

async function deleteFavorite(id) {
    try {
        show("#favoriteResult", await api(`/api/favorites/${id}`, { method: "DELETE" }));
        await listFavorites();
    } catch (error) {
        show("#favoriteResult", error.message);
    }
}

document.addEventListener("DOMContentLoaded", listFavorites);
