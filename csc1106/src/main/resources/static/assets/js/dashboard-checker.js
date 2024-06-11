let sidebarNav = document.getElementById('sidebar-nav');
let currentUrl = window.location.href;
let signOutButton = document.getElementById('signoutButton');

signOutButton.addEventListener('click', function(){
    deleteCookie('lrnznth_User_Cookie');
    deleteCookie('lrnznth_User_Name');
    window.location.href = '/logout';
});

document.addEventListener('DOMContentLoaded', async function(){

    await redirectUserToCorrectDashboard();

});

// Check if cookie exists and return the user name
async function checkCookieReturnName(cookie){
    const response = await fetch('/checkmycookie', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({cookie: cookie})
    });

    const responseText = await response.text();

    if(response.status === 200) return responseText;
    else return null;
}

// Check if cookie exists and return the user object
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

// Function to fetch the dashboard items from the server
async function getDashboardItems(){
    const response = await fetch('/dashboard/dashboard-sidebar-items', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    });

    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    } else {
        const data = await response.json();
        return data;
    }
}

// Will check if user is signed in and set the username in the dashboard
async function userSigned(){

    try {
        // Get Cookies
        const UserCookie = getCookie('lrnznth_User_Cookie');
        const UserName = getCookie('lrnznth_User_Name');

        //If UserCookie does not exist, delete Name Cookie and redirect to login page
        if(!UserCookie){
            deleteCookie('lrnznth_User_Name');
            window.location.href = '/login';
        } 
        
        // If UserCookie exists but UserName does not, check cookie and set Name Cookie
        if(UserCookie && !UserName){
            const responseText = await checkCookieReturnName(UserCookie);
            // User Name Cookie is set to 1 day as we need to check cookie again day by day (Security Reasons)
            if(responseText) setCookie('lrnznth_User_Name', responseText, 1); 
        }

        // Since we have both UserCookie and UserName, we can set the username in the dashboard
        if(UserName){
            document.getElementById('loggedInUsername').innerText = UserName;
        }
    } catch (error) {
        console.error(error);
    }
    
}

// Only for General Dashboard (Currently)
// Refresh Dashboard Sidebar Items by retrieving from the server and set cookies
async function refreshDashboardSideBarItems(){

    try {
        // Get Dashboard Items
        const dashboardItems = await getDashboardItems();
        setCookie('lrnznth_Dashboard_Items_Count', dashboardItems.length, 1);
        
        // Clear Sidebar Nav
        sidebarNav.innerHTML = '';

        let itemsFullString = '';

        dashboardItems.sort((a, b) => a.index - b.index);

        for(let i = 0; i < dashboardItems.length; i++){
            let item = JSON.stringify(dashboardItems[i]);
            
            setCookie(`lrnznth_Dashboard_Item_${i}`, item, 1);
            if(currentUrl.endsWith(dashboardItems[i].urlRedirection)){
                itemsFullString += `
                <li class="nav-item">
                    <a class="nav-link" href="${dashboardItems[i].urlRedirection}">
                        <i class="${dashboardItems[i].icon}"></i>
                        <span>${dashboardItems[i].name}</span>
                    </a>
                </li>
                `;
            }
            else if(dashboardItems[i].icon === 'nav-heading'){
                itemsFullString += `
                <li class="nav-heading">${dashboardItems[i].name}</li>
                `;
            }
            else{
                itemsFullString += `
                <li class="nav-item">
                    <a class="nav-link collapsed" href="${dashboardItems[i].urlRedirection}">
                        <i class="${dashboardItems[i].icon}"></i>
                        <span>${dashboardItems[i].name}</span>
                    </a>
                </li>
                `;
            }
            
        }

        sidebarNav.innerHTML = itemsFullString;
    } catch (error) {
        console.error(error);
    }
    
}

// Only for General Dashboard (Currently)
// Refresh Dashboard Sidebar Items by retrieving from the cookies
async function refreshDashboardSideBarItemsLocally(){
    try {
        let itemsFullString = '';
        sidebarNav.innerHTML = '';
        let itemsCount = getCookie('lrnznth_Dashboard_Items_Count');
        for(let i = 0; i < itemsCount; i++){
            let item = JSON.parse(getCookie(`lrnznth_Dashboard_Item_${i}`));
            console.log(item)
            if(currentUrl.endsWith(item.urlRedirection)){
                itemsFullString += `
                <li class="nav-item">
                    <a class="nav-link" href="${item.urlRedirection}">
                        <i class="${item.icon}"></i>
                        <span>${item.name}</span>
                    </a>
                </li>
                `;
            }
            else if(item.icon === 'nav-heading'){
                itemsFullString += `
                <li class="nav-heading">${item.name}</li>
                `;
            }
            else{
                itemsFullString += `
                <li class="nav-item">
                    <a class="nav-link collapsed" href="${item.urlRedirection}">
                        <i class="${item.icon}"></i>
                        <span>${item.name}</span>
                    </a>
                </li>
                `;
            }
        }
        sidebarNav.innerHTML = itemsFullString;
    } catch (error) {
        console.error(error);
    }
}

async function redirectUserToCorrectDashboard(){

    const userCookie = getCookie('lrnznth_User_Cookie');
    if(!userCookie) return window.location.href = '/login';
    const user = await checkCookieProMax(userCookie);

    // Inner Layer
    // Dashboard Redirect and Do refresh for dashboard items
    if(currentUrl.endsWith('dashboard')) {
        // Check for Admin Role
        if(user.role.roleName === 'Staff') return window.location.href = '/admin';
        // Check for Partner Role
        else if(user.role.roleName === 'Partner') return window.location.href = '/partner';
        // Student & Professor Role Confirmed
        else {
            userSigned();
            if(getCookie('lrnznth_Dashboard_Items_Count')) await refreshDashboardSideBarItemsLocally();
            else await refreshDashboardSideBarItems();
            coverPage(false);
        }
    }

    // Log Out Redirect
    else if(currentUrl.endsWith('logout')) {

        if(!getCookie('lrnznth_User_Cookie')) return window.location.href = '/';

        deleteCookie('lrnznth_User_Cookie');
        deleteCookie('lrnznth_User_Name');
        
        setTimeout(function() {
            window.location.href = "/";
        }, 5000);
        
        return;
    }

    // Admin Dashboard Redirect
    else if(currentUrl.endsWith('admin')) {
        // Check for Partner Role
        if(user.role.roleName === 'Partner') return window.location.href = '/partner';
        // Check for Student & Professor Role
        if(user.role.roleName !== 'Staff') return window.location.href = '/dashboard';

        // Admin Role Confirmed
        // Continue what need to be done in Admin Dashboard
        userSigned();
        coverPage(false);
    }

    // Partner Dashboard Redirect
    else if(currentUrl.endsWith('partner')) {
        // Check for Admin Role
        if(user.role.roleName === 'Staff') return window.location.href = '/admin';
        // Check for Student & Professor Role
        if(user.role.roleName !== 'Partner') return window.location.href = '/dashboard';
        
        // Partner Role Confirmed
        // Continue what need to be done in Partner Dashboard
        userSigned();
        coverPage(false);
    }

    // What need to be done as general
    // currently nothing to need to be done after

}

async function coverPage(boolean){
    if(boolean === true){
        document.getElementById('whitecoverfullscreen').style.display = 'none';
        document.querySelector('iframe[title="loading"]').style.display = 'block';
        return;
    }
    else{
        document.getElementById('whitecoverfullscreen').style.display = 'none';
        document.querySelector('iframe[title="loading"]').style.display = 'none';
        return;
    }
}