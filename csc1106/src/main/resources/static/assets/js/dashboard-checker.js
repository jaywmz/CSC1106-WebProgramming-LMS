let sidebarNav = document.getElementById('sidebar-nav');
let currentUrl = window.location.href;

document.addEventListener('DOMContentLoaded', async function(){

    // If current page is logout page
    if(currentUrl.endsWith('logout')) {

        if(!getCookie('lrnznth_User_Cookie')) return window.location.href = '/';

        deleteCookie('lrnznth_User_Cookie');
        deleteCookie('lrnznth_User_Name');
        
        setTimeout(function() {
            window.location.href = "/";
        }, 5000);
        
        return;
    }
    
    userSigned();
    
    if(getCookie('lrnznth_Dashboard_Items_Count')) await refreshDashboardSideBarItemsLocally();
    else await refreshDashboardSideBarItems();

});

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