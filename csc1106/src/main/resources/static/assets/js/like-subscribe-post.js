async function toggleLike(button, defaultText, activeText, defaultIcon, activeIcon) {
    var likeCountElement = document.getElementById('like-count');

    const path = window.location.pathname.substring(1) + window.location.search;
    const pathArray = path.split('/');
    const postID = pathArray[3];
    const categoryID = pathArray[2];
    const userGroup = pathArray[1];
    const UserName = getCookie('lrnznth_User_Name');
    
    button.classList.toggle('active');
    var buttonText = button.querySelector('span');
    var buttonIcon = button.querySelector('i');

    if (button.classList.contains('active')) {
        const response = await fetch('/community/' + userGroup + '/' + categoryID + '/' + postID + '/like?username=' + UserName, {
            method: 'POST',
        });

        const responseText = await response.text();

        if(response.status === 200) {
            buttonText.textContent = activeText;
            buttonIcon.classList.remove(defaultIcon);
            buttonIcon.classList.add(activeIcon);
            likeCountElement.textContent = parseInt(likeCountElement.textContent) + 1;
            return responseText;
        }
        else {
            console.log("Error liking post");
        }
    } else {
        const response = await fetch('/community/' + userGroup + '/' + categoryID + '/' + postID + '/unlike?username=' + UserName, {
            method: 'POST',
        });

        const responseText = await response.text();

        if(response.status === 200) {
            buttonText.textContent = defaultText;
            buttonIcon.classList.remove(activeIcon);
            buttonIcon.classList.add(defaultIcon);
            likeCountElement.textContent = parseInt(likeCountElement.textContent) - 1;
            return responseText;
        }
        else {
            console.log("Error unliking post");
        }
    }
}

async function toggleSubscribe(button, defaultText, activeText, defaultIcon, activeIcon) {
    const path = window.location.pathname.substring(1) + window.location.search;
    const pathArray = path.split('/');
    const postID = pathArray[3];
    const categoryID = pathArray[2];
    const userGroup = pathArray[1];
    const UserName = getCookie('lrnznth_User_Name');
    
    button.classList.toggle('active');
    var buttonText = button.querySelector('span');
    var buttonIcon = button.querySelector('i');

    if (button.classList.contains('active')) {
        const response = await fetch('/community/' + userGroup + '/' + categoryID + '/' + postID + '/subscribe?username=' + UserName, {
            method: 'POST',
        });

        const responseText = await response.text();

        if(response.status === 200) {
            buttonText.textContent = activeText;
            buttonIcon.classList.remove(defaultIcon);
            buttonIcon.classList.add(activeIcon);
            // likeCountElement.textContent = parseInt(likeCountElement.textContent) + 1;
            return responseText;
        }
        else {
            console.log("Error subscribing to post");
        }
    } else {
        const response = await fetch('/community/' + userGroup + '/' + categoryID + '/' + postID + '/unsubscribe?username=' + UserName, {
            method: 'POST',
        });

        const responseText = await response.text();

        if(response.status === 200) {
            buttonText.textContent = defaultText;
            buttonIcon.classList.remove(activeIcon);
            buttonIcon.classList.add(defaultIcon);
            // likeCountElement.textContent = parseInt(likeCountElement.textContent) - 1;
            return responseText;
        }
        else {
            console.log("Error unliking post");
        }
    }
}