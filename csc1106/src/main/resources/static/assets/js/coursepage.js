const queryParams = new URLSearchParams(window.location.search);
const courseId = queryParams.get('id');
const userId = getCookie('lrnznth_User_ID');
const iframe = document.querySelector('iframe[src="/loading"]');
let addCartButton = document.getElementById('add-to-cart-button');
let markCompleteButton = document.getElementById('mark-complete-button');
let contentCard = document.getElementById('course-content-card');

document.addEventListener('DOMContentLoaded', async () => {
    // console.log("Course ID: " + courseId);
    // console.log("User ID: " + userId);

    // Check if user is already add the course to cart
    const cartCheck = await fetch(`/cartitems/check/${userId}/${courseId}`);
    const cartCheckData = await cartCheck.text();
    if(cartCheckData === 'In cart'){
        addCartButton.style.display = 'block';
        markCompleteButton.style.display = 'none';

        addCartButton.innerHTML = 'Already In Cart';
        addCartButton.disabled = true;
        // Course In user cart (not buy yet)
        if (contentCard) contentCard.remove();
        iframe.remove();
        return;
    } 

    // Check if user is already subscribed to the course
    const subscriptionCheck = await fetch(`/coursesubscriptions/check/${userId}/${courseId}`);
    // Check the response status
    if (subscriptionCheck.status === 200) {
        // If the response status is 200, the user is subscribed to the course
        const subscriptionCheckData = await subscriptionCheck.json();

        // Hide the Add to Cart button as the user bought this course
        addCartButton.style.display = 'none';

        // Show the Mark Complete button
        markCompleteButton.style.display = 'block';

        if(subscriptionCheckData.subscriptionStatus ==="ongoing"){
            markCompleteButton.innerHTML = 'Mark Complete';
            markCompleteButton.disabled = false;

            // Add event listener to Mark Complete button
            markCompleteButton.addEventListener('click', async function(event) {
                const markCompleteResponse = await fetch(`/coursesubscriptions/complete/${userId}/${courseId}`, {
                    method: 'POST'
                });

                if (markCompleteResponse.status === 200) {
                    const markCompleteData = await markCompleteResponse.text();
                    if (markCompleteData === "Course completed"){
                        markCompleteButton.innerHTML = `Completed on ${new Date().toLocaleDateString('en-GB')}`;
                        markCompleteButton.disabled = true;
                    } else {
                        alert("Failed to mark course as completed\n Please try again later");
                    }
                } else {
                    alert("Failed to mark course as completed\n Please try again later");
                }
            });

        }else if (subscriptionCheckData.subscriptionStatus ==="completed"){
            const dateOnly = new Date(subscriptionCheckData.completedDate).toLocaleDateString('en-GB');
            markCompleteButton.innerHTML = `Completed on ${dateOnly}`;
            markCompleteButton.disabled = true;
        }

        // Remove the Loading Iframe
        iframe.remove();
    }
    
    // User not subscribe to the course
    else{
        console.log("User not subscribe to the course");
        addCartButton.style.display = 'block';
        markCompleteButton.style.display = 'none';

        // Delete the content card div
        if (contentCard) contentCard.remove();
        // Remove the Loading Iframe
        iframe.remove();

        // Add event listener to Add to Cart button
        addCartButton.addEventListener('click', async function(event) {
            const addResponse = await fetch(`/cartitems/add/${userId}/${courseId}`);
            if (addResponse.status != 200) {
                return alert("Failed to add course to cart");
            }
            const addData = await addResponse.text();
            if (addData === "Added"){
                alert("Course added to cart");
                addCartButton.innerHTML = 'Already In Cart';
                addCartButton.disabled = true;
            } else {
                alert("Failed to add course to cart");
            }

        });

        // Show the Add to Cart button
        addCartButton.style.display = 'block';
    }
});

document.addEventListener('click', async function(event) {
    // Check if the clicked element is a link
    if (event.target.tagName === 'A') {
        // Prevent the default action to not immediately redirect
        event.preventDefault();

        // Log the href attribute of the clicked link
        console.log(event.target.href);

        // Check if the url link contains "disposition=attachment"
        if (event.target.href.includes("disposition=attachment")) {
            // replace 
            window.location.href = event.target.href.replace("disposition=attachment", "disposition=inline");
        }else if(event.target.href.includes("community")){ // Check if the url link contains "community"
            // Redirect to the community page
            window.location.href = event.target.href;
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

