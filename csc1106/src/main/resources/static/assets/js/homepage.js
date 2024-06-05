let loginButton = document.getElementById('header-login-button');

document.addEventListener('DOMContentLoaded', async function() {
    let userCookie = getCookie('lrnznth_User_Cookie');
    let userName = getCookie('lrnznth_User_Name');

    if (userCookie && userName){
        loginButton.innerHTML = "Dashboard";
        loginButton.href = "/dashboard";
    }else{
        loginButton.innerHTML = "Login";
        loginButton.href = "/login";
    }
});

function getCookie(name) {
    let nameEQ = name + "=";
    let ca = document.cookie.split(';');
    for(let i=0;i < ca.length;i++) {
        let c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1,c.length);
        if (c.indexOf(nameEQ) == 0) {
            let cookieValue = c.substring(nameEQ.length,c.length);
            return decodeURIComponent(cookieValue);
        }
    }
    return null;
}

function deleteCookie(name){
    document.cookie = name + '=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
}

function setCookie(name, value, days){
    let expires = "";
    let encodedValue = encodeURIComponent(value);
    if (days) {
        let date = new Date();
        date.setTime(date.getTime() + (days*24*60*60*1000));
        expires = "; expires=" + date.toUTCString();
    }
    document.cookie = name + "=" + (encodedValue || "")  + expires + "; path=/";
}