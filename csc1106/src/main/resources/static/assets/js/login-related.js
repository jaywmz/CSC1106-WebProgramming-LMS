document.querySelector('#login-form').addEventListener('submit', async function(event) {
    event.preventDefault();
    const form = event.target;
    const formData = new FormData(form);
    const data = {};
    formData.forEach((value, key) => {
        data[key] = value;
    });

    if(data['email'] == '' || data['email'] == null) return console.log('Email is required');

    if(data['password'] == '' || data['password'] == null) return console.log('Password is required');
    
    const rememberMe = data['remember'];

    const thisUser = await logMeIn(data);
    
    // If user is not found
    if(!thisUser) {
        document.querySelector('#login-form').reset();
        return alert('Invalid Email or Password')
    }

    // Set Cookie for user
    if(rememberMe === 'true') {
        setCookie('lrnznth_User_Cookie', thisUser.loginCookie, 365);
        setCookie('lrnznth_User_Name', thisUser.userName, 1);
        setCookie('lrnznth_User_ID', thisUser.userID, 1)
    }
    else{
        setCookie('lrnznth_User_Cookie', thisUser.loginCookie, 1);
        setCookie('lrnznth_User_Name', thisUser.userName, 1);
        setCookie('lrnznth_User_ID', thisUser.userID, 1)
    }
    
    // Redirect user to respective dashboard
    await redirectUserDashboard(thisUser.role.roleName);

});

document.addEventListener('DOMContentLoaded', async function(){
    
    // Get Cookie and check if it is valid
    const thisCheck = getCookie('lrnznth_User_Cookie');
    const user = await checkCookieProMax(thisCheck);
    
    // If user is not found
    if(!user) return deleteCookie('lrnznth_User_Cookie');

    // Redirect user to respective dashboard
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
    // If user is staff, redirect to admin dashboard
    if(userRole === 'Staff') return window.location.href = '/admin';
    // If user is partner, redirect to partner dashboard
    else if(userRole === 'Partner') return window.location.href = '/partner';
    // If user is student & Professor, redirect to general dashboard
    else return window.location.href = '/dashboard';
}