document.querySelector("#loadSido").addEventListener("click", async () => {
    await loadRegion("/api/regions/sido");
});

document.querySelector("#loadSigungu").addEventListener("click", async () => {
    const sidoCode = document.querySelector("#sidoCode").value;
    await loadRegion(`/api/regions/sigungu?sidoCode=${encodeURIComponent(sidoCode)}`);
});

document.querySelector("#loadDong").addEventListener("click", async () => {
    const sigunguCode = document.querySelector("#sigunguCode").value;
    await loadRegion(`/api/regions/dong?sigunguCode=${encodeURIComponent(sigunguCode)}`);
});

document.querySelector("#loadSgis").addEventListener("click", async () => {
    await loadRegion("/api/regions/sgis-token");
});

document.querySelector("#loadVworldSido").addEventListener("click", async () => {
    await loadRegion("/api/regions/vworld/sido");
});

document.querySelector("#loadVworldSigungu").addEventListener("click", async () => {
    const sidoCode = document.querySelector("#sidoCode").value;
    await loadRegion(`/api/regions/vworld/sigungu?sidoCode=${encodeURIComponent(sidoCode)}`);
});

document.querySelector("#loadVworldDong").addEventListener("click", async () => {
    const sigunguCode = document.querySelector("#sigunguCode").value;
    await loadRegion(`/api/regions/vworld/dong?sigunguCode=${encodeURIComponent(sigunguCode)}`);
});

async function loadRegion(url) {
    try {
        const data = await api(url);
        show("#regionResult", data);
    } catch (error) {
        show("#regionResult", error.message);
    }
}
