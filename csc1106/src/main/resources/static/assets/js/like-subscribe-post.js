async function toggleLike(button, defaultText, activeText, defaultIcon, activeIcon) {
    var likeCountElement = document.getElementById('like-count');

    const path = window.location.pathname.substring(1) + window.location.search;
    const pathArray = path.split('/');
    const postID = pathArray[3];
    const categoryID = pathArray[2];
    const userGroup = pathArray[1];
    
    button.classList.toggle('active');
    var buttonText = button.querySelector('span');
    var buttonIcon = button.querySelector('i');

    if (button.classList.contains('active')) {
        buttonText.textContent = activeText;
        buttonIcon.classList.remove(defaultIcon);
        buttonIcon.classList.add(activeIcon);
        likeCountElement.textContent = parseInt(likeCountElement.textContent) + 1;
        
        const response = await fetch('/community/' + userGroup + '/' + categoryID + '/' + postID + '/like', {
            method: 'POST',
        });

        if(response.status === 200) {
            console.log("Post liked")
        }
        else {
            console.log("Error liking post");
        }
    } else {
        buttonText.textContent = defaultText;
        buttonIcon.classList.remove(activeIcon);
        buttonIcon.classList.add(defaultIcon);
        likeCountElement.textContent = parseInt(likeCountElement.textContent) - 1;

        const response = await fetch('/community/' + userGroup + '/' + categoryID + '/' + postID + '/unlike', {
            method: 'POST',
        });

        if(response.status === 200) {
            console.log("Post unliked")
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
    
    button.classList.toggle('active');
    var buttonText = button.querySelector('span');
    var buttonIcon = button.querySelector('i');

    if (button.classList.contains('active')) {
        buttonText.textContent = activeText;
        buttonIcon.classList.remove(defaultIcon);
        buttonIcon.classList.add(activeIcon);
        
        const response = await fetch('/community/' + userGroup + '/' + categoryID + '/' + postID + '/subscribe', {
            method: 'POST',
        });

        if(response.status === 200) {
            console.log("Post subscribed")
        }
        else {
            console.log("Error subscribing to post");
        }
    } else {
        buttonText.textContent = defaultText;
        buttonIcon.classList.remove(activeIcon);
        buttonIcon.classList.add(defaultIcon);
        
        const response = await fetch('/community/' + userGroup + '/' + categoryID + '/' + postID + '/unsubscribe', {
            method: 'POST',
        });

        if(response.status === 200) {
            console.log("Post unsubscribed")
        }
        else {
            console.log("Error unsubscribing post");
        }
    }
}