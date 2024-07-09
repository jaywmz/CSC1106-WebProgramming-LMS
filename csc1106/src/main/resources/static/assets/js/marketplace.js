document.addEventListener("DOMContentLoaded", function() {
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
    }
});

function redirectToPayPal() {
    const userId = document.getElementById("userId").value;
    const amount = document.getElementById("topUpAmount").value;
    if (amount && !isNaN(amount)) {
        window.location.href = `/paypal/pay?total=${amount}&userId=${userId}`;
    } else {
        alert("Please enter a valid amount.");
    }
}
