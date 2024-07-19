let sidebarNav = document.getElementById('sidebar-nav');
let currentUrl = window.location.href;

let sideBarJSON = [
    // {"index": 0, "name": "Dashboard", "icon": "bi bi-grid", "urlRedirection": "/dashboard"},
    {"index": 0, "name": "My Learning", "icon": "bi bi-vector-pen", "urlRedirection": "/mylearning"},
    {"index": 1, "name": "Marketplace", "icon": "bi bi-shop", "urlRedirection": "/market"},
    {"index": 2, "name": "Community", "icon": "bi bi-people-fill", "urlRedirection": "/community"},
    // {"index": 3, "name": "My Learning", "icon": "bi bi-vector-pen", "urlRedirection": "/mylearning"},
    {"index": 4, "name": "User", "icon": "nav-heading", "urlRedirection": null},
    {"index": 5, "name": "My Cart", "icon": "bi bi-cart-fill", "urlRedirection": "/cart"},
    {"index": 6, "name": "User", "icon": "bi bi-person-circle", "urlRedirection": "/userprofile"},
    {"index": 7, "name": "F.A.Q", "icon": "bi bi-info-square", "urlRedirection": "/faq"},
    {"index": 8, "name": "Contact Us", "icon": "bi bi-envelope", "urlRedirection": "/contact"},
    // {"index": 9, "name": "Extra (Just For Reference)", "icon": "nav-heading", "urlRedirection": null},
    // {"index": 10, "name": "BLANK PAGE", "icon": "bi bi-file-earmark-lock2", "urlRedirection": "/blank"},
    // {"index": 11, "name": "Refresh SideBar", "icon": "bi bi-arrow-repeat", "urlRedirection": "/refreshsidebar"}
]



///////////////////////// EVENT LISTENERS /////////////////////////

document.addEventListener('DOMContentLoaded', async function(){
    await logoutChecker();
    await redirectUserToCorrectDashboard();
    await dashboardRefreshItems();

});




//////////////////////////// FUNCTIONS ////////////////////////////


// Check if cookie exists and 
// return the user name (string)
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

// Check if cookie exists and 
// return the user object (Obj)
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

// Get Cookie Function
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

// Delete Cookie Function
function deleteCookie(name){
    document.cookie = name + '=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
}

// Set Cookie Function
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

// Function to fetch the dashboard sidebar items from the server
// Return sidebar items (Array)
async function getDashboardItems(){
    return sideBarJSON;
}

// Will check if user is signed in and set the username in the dashboard
// If user is not signed in, redirect to login page
// If user is signed in but username is not set, check cookie and set Name Cookie
// Not returning anything
async function userSigned(){

    try {
        // Get Cookies
        const UserCookie = getCookie('lrnznth_User_Cookie');
        const UserName = getCookie('lrnznth_User_Name');
        const UserId = getCookie('lrnznth_User_ID');

        //If UserCookie does not exist, delete Name Cookie and redirect to login page
        if(!UserCookie){
            deleteCookie('lrnznth_User_Name');
            window.location.href = '/login';
        } 
        
        // If UserCookie exists but UserName does not, check cookie and set Name Cookie
        if(!UserName || !UserId){
            const user = await checkCookieProMax(UserCookie);
            // User Name Cookie is set to 1 day as we need to check cookie again day by day (Security Reasons)
            setCookie('lrnznth_User_Name', user.userName, 1);
            setCookie('lrnznth_User_ID', user.userID, 1); 
        }

        // Since we have both UserCookie and UserName, we can set the username in the dashboard
        if(UserName){
            document.getElementById('loggedInUsername').innerText = UserName;
        }
    } catch (error) {
        console.log("Error in userSigned() function, ignore if needed")
        console.log(error);
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
        console.log("Error in refreshDashboardSideBarItems() function, ignore if needed");
        console.log(error);
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
            // console.log(item)
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
        console.log("Error in refreshDashboardSideBarItemsLocally() function, ignore if needed")
        console.log(error);
    }
}

// Unhide/Hide Loading Div (True / False)
async function coverPage(boolean){

    try {
        if(!document.getElementById('whitecoverfullscreen') && !document.querySelector('iframe[title="loading"]')) return;

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
    } catch (error) {
        console.log("Error in coverPage() function, ignore if needed")
        console.log(error);
    }
    
}

// Refresh dashboard items 
async function dashboardRefreshItems(){
    await userSigned()
    
    // if(getCookie('lrnznth_Dashboard_Items_Count')) await refreshDashboardSideBarItemsLocally();
    // else await refreshDashboardSideBarItems();

    // if cookie does not already have side bar items added, the refreshDashboardSideBarItemsLocally() function won't load sidebar because it does not add sidebar items to cookie first.
    refreshDashboardSideBarItems();

    await coverPage(false);
}

// A function that will only activate when the user is in the logout page
// A function that will add event listener to sign out button 
async function logoutChecker(){

    if(currentUrl.endsWith('logout')) {
        if(!getCookie('lrnznth_User_Cookie')) return window.location.href = '/';

        deleteCookie('lrnznth_User_Cookie');
        deleteCookie('lrnznth_User_Name');
        deleteCookie('lrnznth_User_ID');
        
        setTimeout(function() {
            window.location.href = "/";
        }, 5000);
        
        return;
    }

    let signOutButton = document.getElementById('signoutButton');

    if(signOutButton){
        signOutButton.addEventListener('click', async function(){
            deleteCookie('lrnznth_User_Cookie');
            deleteCookie('lrnznth_User_Name');
            deleteCookie('lrnznth_User_ID');
            window.location.href = '/logout';
        });
    }
} 



// Control the redirection of the user according to the role
async function redirectUserToCorrectDashboard(){

    // Get User Cookie
    const userCookie = getCookie('lrnznth_User_Cookie');
    // Get User Id
    const userId = getCookie('lrnznth_User_ID');

    // If User Cookie does not exist, redirect to login page
    if(!userCookie) return window.location.href = '/login';
    
    // Check User Cookie and get User Object
    const user = await checkCookieProMax(userCookie);

    // Get User Role (Staff/Partner/Student/Instructor)
    const userRole = user.role.roleName;

    // Get User Email
    const userEmail = user.email;

    // Set A list of route for each role
    // For Admin
    let adminRoute = [
        'admin'
    ];
    // For Partner
    let partnerRoute = [
        'partner',
        'partnership',
        'partnerDashboard', // Assuming 'partnerDashboard' is the route after redirection
        'renewExpired' // Assuming 'renewExpired' is the route for renewing subscriptions

    ];
    // For Student & Instructor
    let userRoute = [
        'dashboard',
        'market',
        'category',
        'community',
        'mylearning',
        'cart',
        'userprofile',
        'contact'
    ];

    // Get the true route
    // Example: "/dashboard", "/community", "course/123/...."
    const urlSegment = currentUrl.replace(/^https?:\/\/[^\/]+\//, '').split('/')[0];
    
    // If user is admin
    if(userRole === 'Admin'){
        if(!adminRoute.includes(urlSegment)) return window.location.href = '/admin';
    }
    // If user is partner
    else if(userRole === 'Partner'){

        //log
        console.log('User is a partner');
       
        if (!partnerRoute.includes(urlSegment)) return window.location.href = '/partner'; // Redirect to partnerDashboard

         // Check if partnership is expired
         const expirationInfo = await partnershipIsExpired(userId);

         if (expirationInfo.isExpired) {
             return window.location.href = '/partnership/renewExpired'; // Redirect to renewExpired if partnership is expired
         } else if (expirationInfo.willExpireSoon) {
             alert('Your partnership is going to expire in less than 30 days. Please renew soon - under Renew Partnership Subscription Tab.');
         }

    }
    // If user is student or instructor
    else if(userRole === 'Student' || userRole === 'Instructor'){
        if(!userRoute.includes(urlSegment)) return window.location.href = '/dashboard';
    }
}

// Check if partnership is expired or will expire soon
async function partnershipIsExpired(userId) {
    try {
        const response = await fetch('/partnership/checkExpired?userId=' + userId, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();

        const isExpired = data.isExpired;
        const willExpireSoon = data.willExpireSoon;

        if (isExpired) {
            setCookie('lrnznth_PartnerShip', 'true', 1);
        }

        return { isExpired, willExpireSoon };
    } catch (error) {
        console.error('Error checking partnership expiration:', error);
        return { isExpired: false, willExpireSoon: false }; // Handle error case, assuming partnership is not expired or will expire soon
    }
}

