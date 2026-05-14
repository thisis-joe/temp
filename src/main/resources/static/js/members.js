document.querySelector("#accountLogin")?.addEventListener("click", () => openAuthModal("login"));
document.querySelector("#accountRegister")?.addEventListener("click", () => openAuthModal("register"));

document.querySelector("#me")?.addEventListener("click", async () => {
    try {
        show("#memberResult", await api("/api/auth/me"));
    } catch (error) {
        show("#memberResult", error.message);
    }
});

document.querySelector("#logout")?.addEventListener("click", async () => {
    try {
        show("#memberResult", await api("/api/auth/logout", { method: "POST" }));
        await renderSession();
    } catch (error) {
        show("#memberResult", error.message);
    }
});
