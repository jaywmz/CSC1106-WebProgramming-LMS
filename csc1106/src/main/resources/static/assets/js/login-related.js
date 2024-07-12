document.querySelector('#login-form').addEventListener('submit', async function(event) {
    event.preventDefault();
    const form = event.target;
    const formData = new FormData(form);
    const data = {};
    formData.forEach((value, key) => {
        data[key] = value;
    });

    if(data['email'] == '' || data['email'] == null) {
        console.log('Email is required');
        return;
    }

    if(data['password'] == '' || data['password'] == null) {
        console.log('Password is required');
        return;
    }

    const rememberMe = data['remember'];

    const thisUser = await logMeIn(data);

    if(!thisUser) {
        document.querySelector('#login-form').reset();
        alert('Invalid Email or Password');
        return;
    }

    if(rememberMe === 'true') {
        setCookie('lrnznth_User_Cookie', thisUser.loginCookie, 365);
    } else {
        setCookie('lrnznth_User_Cookie', thisUser.loginCookie, 1);
    }

    setCookie('lrnznth_User_Name', thisUser.userName, 1);
    setCookie('lrnznth_User_ID', thisUser.userID, 1);

    await redirectUserDashboard(thisUser.role.roleName);
});

document.addEventListener('DOMContentLoaded', async function(){
    const thisCheck = getCookie('lrnznth_User_Cookie');
    if (!thisCheck) return;
    
    const user = await checkCookieProMax(thisCheck);

    if(!user) {
        deleteCookie('lrnznth_User_Cookie');
        return;
    }

    await redirectUserDashboard(user.role.roleName);
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
    document.cookie = name + '=; expires=Thu, 01 Jan 1970 00:00:01 GMT; path=/';
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

async function logMeIn(data){
    try {
        const response = await fetch('/logmein', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const user = await response.json();
        return user;

    } catch (error) {
        console.error('There was a problem with the fetch operation: ' + error.message);
    }
}

async function checkCookieProMax(cookie){
    try {
        const response = await fetch('/checkmycookiepromax', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({cookie: cookie})
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const user = await response.json();

        return user;
    } catch (error) {
        console.error('There was a problem with the fetch operation: ' + error.message);
    }
}

async function redirectUserDashboard(userRole){
    if(userRole === 'Staff') {
        window.location.href = '/admin';
    } else if(userRole === 'Partner') {
        window.location.href = '/partner';
    } else {
        window.location.href = '/dashboard';
    }
}
