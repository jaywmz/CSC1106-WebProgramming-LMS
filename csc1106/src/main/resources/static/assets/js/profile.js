let roleSpan = document.getElementById("role-nice-card");
let joinedSpan = document.getElementById("joined-nice-card");
let balanceSpan = document.getElementById("balance-nice-card");
let loadingDiv = document.querySelector("#loading-div-hidden");
let loadingText = document.querySelector("#loading-text");

let usernameInput = document.getElementById("username");
let emailInput = document.getElementById("email");
let passwordInput = document.getElementById("password");

let saveButton = document.getElementById("saveBtn");

let fileUploadDiv = document.getElementById("file-upload-div");
let webcamTipsDiv = document.getElementById("webcam-tips-div");
let takePictureButton = document.getElementById("take-picture-btn");
let imageUploadButton = document.getElementById("uploadThisImageButton");
let uploadWebcamButton = document.getElementById("uploadWebcamImageButton");
let userProfileImage = document.getElementById("user-profile-image");
let webcamButton = document.getElementById("webcam-btn");

let webcamActive = false;
let streamReference = null;

document.getElementById("togglePassword").addEventListener("click", function() {
    let passwordInput = document.getElementById("password");
    let passwordIcon = this.getElementsByTagName("i")[0];
    if (passwordInput.type === "password") {
        passwordInput.type = "text";
        passwordIcon.classList.remove("fa-eye");
        passwordIcon.classList.add("fa-eye-slash");
    } else {
        passwordInput.type = "password";
        passwordIcon.classList.remove("fa-eye-slash");
        passwordIcon.classList.add("fa-eye");
    }
});

document.addEventListener('DOMContentLoaded', async () => {
    const userCookie = getCookie('lrnznth_User_Cookie');
    const userID = getCookie('lrnznth_User_ID');
    if (!userCookie) {
        window.location.href = '/login';
    }
    const user = await checkCookieProMax(userCookie);
    if (!user) {
        window.location.href = '/login';
    }

    usernameInput.value = user.userName;
    emailInput.value = user.userEmail;
    passwordInput.value = user.userPassword;
    roleSpan.innerHTML = user.role.roleName;
    joinedSpan.innerHTML = user.joinedDate;
    balanceSpan.innerHTML = user.userBalance;

    const signedUrl = await getSignedUserProfileImage(userID);
    if (signedUrl) {
        userProfileImage.src = signedUrl;
    }
    else {
        userProfileImage.src = "assets/img/profile-img.jpg";
    }

    
});

saveButton.addEventListener("click", async function(event) {
    event.preventDefault();
    const userCookie = getCookie('lrnznth_User_Cookie');
    let user = await checkCookieProMax(userCookie);
    user.userEmail = emailInput.value;
    user.userPassword = passwordInput.value;
    try {
        const response = await fetch(`/updateuser`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(user)
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const reply = await response.json();
        if(reply) {
            alert("User updated successfully");
            window.location.reload();
        }
    } catch (error) {
        return alert("Failed to update user");
    }
});

uploadWebcamButton.addEventListener("click", async function(event) {
    event.preventDefault();

    showLoadingDiv(true);
    setLoadingText("Loading...");
   
    const canvas = document.getElementById('canvas');
    const dataURL = canvas.toDataURL();
    const blob = await fetch(dataURL).then(res => res.blob());
    const formData = new FormData();
    formData.append('file', blob);

    const userID = getCookie('lrnznth_User_ID');

    if (!userID) {
        showLoadingDiv(false);
        return alert("User not found, Please Re-login to continue");
    }

    formData.append('userId', userID);

    setLoadingText("Uploading Image...");

    // It will return the URL of the image (Unsigned URL - Unable to access publicly and directly)
    const returnUrl = await uploadImage(formData);
    if (!returnUrl) {
        showLoadingDiv(false);
        return alert("Failed to upload image");
    }

    setLoadingText("Saving Image...");

    //Sign the URL to access the image
    const signedUrl = await getSignedUserProfileImage(userID);
    if (!signedUrl) {
        showLoadingDiv(false);
        return alert("Failed to sign image");
    }

    userProfileImage.src = signedUrl;
    await closeUploadModal();
    setLoadingText("Loading...");
    showLoadingDiv(false);
});

imageUploadButton.addEventListener("click", async function(event) {
    event.preventDefault();

    showLoadingDiv(true);
    setLoadingText("Loading...");

    const input = document.getElementById("file-upload");
    const file = input.files[0];
    if (!file) {
        showLoadingDiv(false);
        return alert("Please select a file");
    }

    // if file size is greater than 5MB
    if (file.size > 5242880) {
        showLoadingDiv(false);
        return alert("File size is too large. Please select a file less than 5MB");
    }

    const formData = new FormData();
    formData.append('file', file);

    const userID = getCookie('lrnznth_User_ID');

    if (!userID) {
        showLoadingDiv(false);
        return alert("User not found, Please Re-login to continue");
    }

    formData.append('userId', userID);

    setLoadingText("Uploading Image...");
    
    // It will return the URL of the image (Unsigned URL - Unable to access publicly and directly)
    const returnUrl = await uploadImage(formData);
    if (!returnUrl) {
        showLoadingDiv(false);
        return alert("Failed to upload image");
    }

    setLoadingText("Saving Image...");

    //Sign the URL to access the image
    const signedUrl = await getSignedUserProfileImage(userID);
    if (!signedUrl) {
        showLoadingDiv(false);
        return alert("Failed to sign image");
    }

    userProfileImage.src = signedUrl;
    await closeUploadModal();
    setLoadingText("Loading...");
    showLoadingDiv(false);
});

webcamButton.addEventListener('click', function() {
    const video = document.getElementById('webcam-feed');
    const canvas = document.getElementById('canvas');
    
    if (!webcamActive) {
        // Access the webcam
        fileUploadDiv.style.display = 'none';
        webcamTipsDiv.style.display = 'block';
        takePictureButton.style.display = 'block';
        imageUploadButton.style.display = 'none';
        uploadWebcamButton.style.display = 'block';
        navigator.mediaDevices.getUserMedia({ video: true })
            .then(function(stream) {
            streamReference = stream;
            video.srcObject = stream;
            video.style.display = 'block';
            canvas.style.display = 'block'; // Show canvas when webcam is active
            webcamActive = true;
            document.getElementById('webcam-btn').textContent = 'Close Webcam';
            })
            .catch(function(error) {
            console.log("Error accessing the webcam: ", error);
            });
    } else {
        // Stop the webcam
        fileUploadDiv.style.display = 'block';
        webcamTipsDiv.style.display = 'none';
        takePictureButton.style.display = 'none';
        imageUploadButton.style.display = 'block';
        uploadWebcamButton.style.display = 'none';
        if (streamReference) {
            streamReference.getTracks().forEach(track => track.stop());
        }
        video.style.display = 'none';
        canvas.style.display = 'none'; // Hide canvas when webcam is not active
        webcamActive = false;
        document.getElementById('webcam-btn').textContent = 'Open Webcam';
    }
});

// Capture the image when the "Take Picture" button is clicked
takePictureButton.addEventListener('click', function() {
    const canvas = document.getElementById('canvas');
    const video = document.getElementById('webcam-feed');
    if (video.srcObject) {
      canvas.width = video.videoWidth;
      canvas.height = video.videoHeight;
      canvas.getContext('2d').drawImage(video, 0, 0, canvas.width, canvas.height);
      // Optionally, display the captured image in an img element or use the canvas.toDataURL() for other purposes
    }
});

async function uploadImage(formData){
    try {
        const response = await fetch('/setprofilepicture', {
            method: 'POST',
            body: formData
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const reply = await response.text();
        return reply;
    }
    catch (error) {
        console.error('Error:', error);
        return null;
    }
}

async function getSignedUserProfileImage(userId){
    try {
        const response = await fetch(`/getprofilepicture/${userId}`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const reply = await response.text();
        return reply;
    }
    catch (error) {
        console.error("This Error might occurred due to the user not having a profile picture");
        console.error('Error:', error);
        return null;
    }
}

async function closeUploadModal() {
    let uploadModal = bootstrap.Modal.getInstance(document.getElementById("uploadModal"));
    uploadModal.hide();
}

function showLoadingDiv(boolean){
    if(boolean === true){
        loadingDiv.style.display = "block";
    } else {
        loadingDiv.style.display = "none";
    }
}

function setLoadingText(text){
    loadingText.textContent = text;
}