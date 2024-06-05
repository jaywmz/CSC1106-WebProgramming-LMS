document.querySelector('#login-form').addEventListener('submit', function(event) {
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

    fetch('/logmein', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
    .then(async response => {

        const responseText = await response.text();
        if(response.status === 200) {
            
            console.log(responseText);
            if(rememberMe === 'true'){
                // If remember me is checked, set cookie to 365 days
                setCookie('lrnznth_User_Cookie', responseText, 365);
            }
            else{
                // If remember me is not checked, set cookie to 1 day
                setCookie('lrnznth_User_Cookie', responseText, 1);
            }

            window.location.href = '/dashboard';

        } else {
            alert(responseText);
            document.querySelector('#login-form').reset();
        }
    }).catch(error => {
        console.log(error);
    });        
});

document.addEventListener('DOMContentLoaded', async function(){
    
    // Get Cookie and check if it is valid
    const thisCheck = getCookie('lrnznth_User_Cookie');
    
    if(thisCheck){

        // Check if cookie is valid
        const response = await fetch('/checkmycookie', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({cookie: thisCheck})
        });

        // Wait for response
        const responseText = await response.text();

        // If response is 200, redirect to dashboard, set Name Cookie (1 Days)
        if(response.status === 200){
            setCookie('lrnznth_User_Name', responseText, 1);
            window.location.href = '/dashboard';
        }
        // Remove Cookie if it is not valid
        else{
            deleteCookie('lrnznth_User_Cookie');
        }
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