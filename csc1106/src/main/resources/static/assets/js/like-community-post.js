async function toggleActive(button, defaultText, activeText, defaultIcon, activeIcon) {
    var likeCountElement = document.getElementById('like-count');
    const path = window.location.pathname.substring(1) + window.location.search;
    console.log(path);
    const pathArray = path.split('/');
    pathArray.forEach(pathVar => {
        console.log(pathVar);
    });
    const postID = pathArray[3];
    const categoryID = pathArray[2];
    const userGroup = pathArray[1];
    
    button.classList.toggle('active');
    var buttonText = button.querySelector('span');
    var buttonIcon = button.querySelector('i');
    if (button.classList.contains('active')) {
        const response = await fetch('/community/' + userGroup + '/' + categoryID + '/' + postID + '/like', {
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
    } else {
        const response = await fetch('/community/' + userGroup + '/' + categoryID + '/' + postID + '/unlike', {
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
    }
}