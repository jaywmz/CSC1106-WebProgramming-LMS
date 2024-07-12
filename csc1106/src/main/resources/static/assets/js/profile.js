let roleSpan = document.getElementById("role-nice-card");
let joinedSpan = document.getElementById("joined-nice-card");
let balanceSpan = document.getElementById("balance-nice-card");

let usernameInput = document.getElementById("username");
let emailInput = document.getElementById("email");
let passwordInput = document.getElementById("password");

let saveButton = document.getElementById("saveBtn");

document.getElementById("togglePassword").addEventListener("click", function() {
    let passwordInput = document.getElementById("password");
    let passwordIcon = this.getElementsByTagName("i")[0];
    if (passwordInput.type === "password") {
        passwordInput.type = "text";
        passwordIcon.classList.remove("fa-eye");
        passwordIcon.classList.add("fa-eye-slash");
    } else {
        passwordInput.type = "password";
        passwordIcon.classList.remove("fa-eye-slash");
        passwordIcon.classList.add("fa-eye");
    }
});

document.addEventListener('DOMContentLoaded', async () => {
    const userCookie = getCookie('lrnznth_User_Cookie');
    if (!userCookie) {
        window.location.href = '/login';
    }
    const user = await checkCookieProMax(userCookie);
    if (!user) {
        window.location.href = '/login';
    }

    usernameInput.value = user.userName;
    emailInput.value = user.userEmail;
    passwordInput.value = user.userPassword;
    roleSpan.innerHTML = user.role.roleName;
    joinedSpan.innerHTML = user.joinedDate;
    balanceSpan.innerHTML = user.userBalance;
});

saveButton.addEventListener("click", async function(event) {
    event.preventDefault();
    const userCookie = getCookie('lrnznth_User_Cookie');
    let user = await checkCookieProMax(userCookie);
    user.userEmail = emailInput.value;
    user.userPassword = passwordInput.value;
    try {
        const response = await fetch(`/updateuser`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(user)
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const reply = await response.json();
        if(reply) {
            alert("User updated successfully");
            window.location.reload();
        }
    } catch (error) {
        return alert("Failed to update user");
    }
});