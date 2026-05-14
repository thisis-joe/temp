document.querySelector("#addFavorite").addEventListener("click", async () => {
    try {
        show("#favoriteResult", await api("/api/favorites", {
            method: "POST",
            body: JSON.stringify({
                sidoNm: document.querySelector("#sidoNm").value,
                sigunguNm: document.querySelector("#sigunguNm").value,
                dongNm: document.querySelector("#dongNm").value,
                lawdCd: document.querySelector("#favLawdCd").value,
                memo: document.querySelector("#memo").value
            })
        }));
    } catch (error) {
        show("#favoriteResult", error.message);
    }
});

document.querySelector("#listFavorite").addEventListener("click", async () => {
    try {
        show("#favoriteResult", await api("/api/favorites"));
    } catch (error) {
        show("#favoriteResult", error.message);
    }
});
