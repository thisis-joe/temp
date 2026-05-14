document.querySelector("#register").addEventListener("click", async () => {
    try {
        show("#memberResult", await api("/api/auth/register", {
            method: "POST",
            body: JSON.stringify(memberPayload())
        }));
        renderSession();
    } catch (error) {
        show("#memberResult", error.message);
    }
});

document.querySelector("#login").addEventListener("click", async () => {
    try {
        show("#memberResult", await api("/api/auth/login", {
            method: "POST",
            body: JSON.stringify({
                email: document.querySelector("#email").value,
                password: document.querySelector("#password").value
            })
        }));
        renderSession();
    } catch (error) {
        show("#memberResult", error.message);
    }
});

document.querySelector("#me").addEventListener("click", async () => {
    try {
        show("#memberResult", await api("/api/auth/me"));
    } catch (error) {
        show("#memberResult", error.message);
    }
});

document.querySelector("#logout").addEventListener("click", async () => {
    try {
        show("#memberResult", await api("/api/auth/logout", { method: "POST" }));
        renderSession();
    } catch (error) {
        show("#memberResult", error.message);
    }
});

document.querySelector("#listMembers").addEventListener("click", async () => {
    const params = new URLSearchParams();
    const keyword = document.querySelector("#memberKeyword").value;
    if (keyword) {
        params.set("keyword", keyword);
    }
    try {
        show("#memberListResult", await api(`/api/members?${params}`));
    } catch (error) {
        show("#memberListResult", error.message);
    }
});

function memberPayload() {
    return {
        email: document.querySelector("#email").value,
        password: document.querySelector("#password").value,
        name: document.querySelector("#name").value,
        phone: document.querySelector("#phone").value,
        address: document.querySelector("#address").value
    };
}
