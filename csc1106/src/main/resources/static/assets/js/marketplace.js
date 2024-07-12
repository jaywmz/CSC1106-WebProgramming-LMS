document.addEventListener("DOMContentLoaded", function() {
    // Fetch supported currencies from the server
    fetch('/currencies')
        .then(response => response.json())
        .then(data => {
            let currencySelect = document.getElementById('currency');
            data.forEach(currency => {
                let option = document.createElement('option');
                option.value = currency;
                option.text = currency;
                currencySelect.appendChild(option);
            });
        })
        .catch(error => console.error('Error fetching currencies:', error));

    // Existing code...
    fetch('/market/totalApprovedCourses')
        .then(response => response.json())
        .then(data => {
            document.getElementById('totalCourses').innerText = data;
        });

    fetch('/market/categories')
        .then(response => response.json())
        .then(data => {
            let categoriesContainer = document.getElementById('categories');
            data.forEach(category => {
                let categoryElement = document.createElement('div');
                categoryElement.classList.add('col-md-6', 'mb-4');
                categoryElement.innerHTML = `
                    <a href="/category/${category.id}" class="card-link">
                        <div class="card category-card card-custom d-flex flex-row align-items-center">
                            <div class="category-image-wrapper">
                                <img src="${category.coverImageUrl}" class="category-image" alt="Category Image" onerror="this.onerror=null;this.src='/assets/img/category-placeholder.jpg';">
                            </div>
                            <div class="card-body">
                                <h5 class="card-title">${category.name}</h5>
                                <p class="card-text">${category.description}</p>
                            </div>
                        </div>
                    </a>
                `;
                categoriesContainer.appendChild(categoryElement);
            });
        });

    function getCookie(name) {
        let cookieArr = document.cookie.split(";");
        for (let i = 0; i < cookieArr.length; i++) {
            let cookiePair = cookieArr[i].split("=");
            if (name == cookiePair[0].trim()) {
                return decodeURIComponent(cookiePair[1]);
            }
        }
        return null;
    }

    let userId = getCookie("userId");
    if (userId) {
        document.getElementById("userId").value = userId;

        fetch(`/user/${userId}/balance`)
            .then(response => response.json())
            .then(data => {
                document.getElementById("currentBalance").innerText = `$${data.balance.toFixed(2)}`;
            })
            .catch(error => {
                console.error('Error fetching balance:', error);
            });

        fetch(`/user/${userId}/role`)
            .then(response => response.json())
            .then(data => {
            console.log('Fetched role:', data.role);  // Add this line for logging
        if (data.role.toLowerCase() !== 'professor') {  // Ensure case-insensitive comparison
            document.getElementById("uploadCourseLink").addEventListener("click", function(event) {
                event.preventDefault();
                showModal("You do not have the rights to upload a course.");
            });
            document.getElementById("viewCoursesLink").addEventListener("click", function(event) {
                event.preventDefault();
                showModal("You do not have the rights to view courses.");
            });
        }
    })
    .catch(error => {
        console.error('Error fetching user role:', error);
    });

        
    }

    function showModal(message) {
        const modal = document.getElementById('errorModal');
        const modalMessage = document.getElementById('modalMessage');
        modalMessage.innerText = message;
        const bootstrapModal = new bootstrap.Modal(modal);
        bootstrapModal.show();
    }
});

function redirectToPayPal() {
    const userId = document.getElementById("userId").value;
    const amount = document.getElementById("topUpAmount").value;
    const currency = document.getElementById("currency").value;
    if (amount && !isNaN(amount) && currency) {
        window.location.href = `/paypal/pay?total=${amount}&currency=${currency}&userId=${userId}`;
    } else {
        alert("Please enter a valid amount and select a currency.");
    }
}
