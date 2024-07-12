document.addEventListener('DOMContentLoaded', function() {
    const sortBySelect = document.getElementById('sort-by');
    const filterCategorySelect = document.getElementById('filter-category');
    const coursesContainer = document.getElementById('courses-list');
    const spinner = document.querySelector('.spinner-border');
    const loggedInUsername = document.getElementById('loggedInUsername');
    const uploadCourseLink = document.querySelector('.nav-item a[href="/upload"]');
    const viewCoursesLink = document.querySelector('.nav-item a[href="/coursesupload"]');
    const currencySelect = document.getElementById('currency'); // Ensure this ID matches your HTML

    // Display user name in the top right corner
    const userName = getCookie('lrnznth_User_Name');
    if (userName) {
        loggedInUsername.textContent = userName;
    } else {
        loggedInUsername.textContent = 'Guest';
    }

    // Fetch supported currencies from the server and populate the currency dropdown
    fetch('/currencies')
        .then(response => response.json())
        .then(data => {
            data.forEach(currency => {
                let option = document.createElement('option');
                option.value = currency;
                option.text = currency;
                currencySelect.appendChild(option);
            });
        })
        .catch(error => console.error('Error fetching currencies:', error));

    sortBySelect.addEventListener('change', loadCourses);
    filterCategorySelect.addEventListener('change', loadCourses);

    async function loadCourses() {
        try {
            var sortBy = sortBySelect.value;
            var categoryId = filterCategorySelect.value || window.location.pathname.split("/").pop();
    
            // Show the loading spinner
            if (spinner) {
                spinner.style.display = 'inline-block';
            }
    
            // Clear the existing courses
            coursesContainer.innerHTML = '';
            coursesContainer.appendChild(spinner);
    
            const response = await fetch(`/category/${categoryId}/courses?sortBy=${sortBy}`);
            
            if (!response.ok) {
                throw new Error('Network response was not ok ' + response.statusText);
            }
    
            const data = await response.json();
            console.log('Fetched data:', data);
    
            // Remove spinner
            coursesContainer.innerHTML = '';
    
            if (!Array.isArray(data)) {
                console.error("Data is not an array: ", data);
                return;
            }
    
            data.forEach(course => {
                let courseElement = document.createElement('div');
                courseElement.classList.add('col-md-4', 'mb-4');
                courseElement.innerHTML = `
                    <div class="card card-custom">
                        <img src="${course.coverImageUrl}" class="card-img-top" alt="Course Image">
                        <div class="card-body">
                            <h5 class="card-title">${course.title}</h5>
                            <p class="card-text">${course.description}</p>
                            <div class="course-rating">
                                <span class="star">&#9733;</span>
                                <span class="reviews">${course.averageRating} (${course.reviewCount} reviews)</span>
                            </div>
                            <p class="card-price"><strong>$${course.price}</strong></p>
                            <button class="btn btn-primary add-to-cart" data-course-id="${course.id}">Add to Cart</button>
                        </div>
                    </div>
                `;
                coursesContainer.appendChild(courseElement);
            });
    
            // Hide the loading spinner
            if (spinner) {
                spinner.style.display = 'none';
            }
    
            // Add event listeners to new Add to Cart buttons
            addCartEventListeners();
        } catch(e) {
            console.error('Error loading courses:', e);
        }
    }
    
    // Add to Cart event listener
    function addCartEventListeners() {
        const addToCartButtons = document.querySelectorAll('.add-to-cart');
        addToCartButtons.forEach(button => {
            button.addEventListener('click', async function(event) {
                const courseId = event.target.getAttribute('data-course-id');
                let userId = getCookie('lrnznth_User_ID');
                
                if (!userId) {
                    alert('Please login to add courses to cart');
                    return;
                }

                // Check if user is already subscribed to the course
                const subscriptionCheck = await fetch(`/coursesubscriptions/check/${userId}/${courseId}`);
                const subscriptionCheckData = await subscriptionCheck.text();

                if(subscriptionCheckData === 'Subscribed'){
                    alert('You have already subscribed to this course');
                    return;
                }

                // Check if the course is already in user's cart
                const cartCheck = await fetch(`/cartitems/check/${userId}/${courseId}`);
                const cartCheckData = await cartCheck.text();
                if(cartCheckData === 'In cart'){
                    alert('Course is already in your cart');
                    return;
                }

                // Add course to cart
                const addResponse = await fetch(`/cartitems/add/${userId}/${courseId}`);
                if (addResponse.status != 200) {
                    return alert("Failed to add course to cart");
                }
                const addData = await addResponse.text();
                if (addData === "Added"){
                    alert("Course added to cart");
                } else {
                    alert("Failed to add course to cart");
                }
            });
        });
    }

    // Check user role and adjust UI accordingly
    let userId = getCookie('lrnznth_User_ID');
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
                if (data.role.toLowerCase() !== 'professor' && data.role.toLowerCase() !== 'instructor') {  // Ensure case-insensitive comparison
                    uploadCourseLink.addEventListener("click", function(event) {
                        event.preventDefault();
                        showModal("You do not have the rights to upload a course.");
                    });
                    viewCoursesLink.addEventListener("click", function(event) {
                        event.preventDefault();
                        showModal("You do not have the rights to view courses.");
                    });
                }
            })
            .catch(error => {
                console.error('Error fetching user role:', error);
            });
    }

    // Initial load
    loadCourses();

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

    function showModal(message) {
        const modal = document.getElementById('errorModal');
        const modalMessage = document.getElementById('modalMessage');
        modalMessage.innerText = message;
        const bootstrapModal = new bootstrap.Modal(modal);
        bootstrapModal.show();
    }

    window.redirectToPayPal = function() {
        const userId = document.getElementById("userId").value;
        const amount = document.getElementById("topUpAmount").value;
        const currency = document.getElementById("currency").value;
        if (amount && !isNaN(amount) && currency) {
            window.location.href = `/paypal/pay?total=${amount}&currency=${currency}&userId=${userId}`;
        } else {
            alert("Please enter a valid amount and select a currency.");
        }
    };
});
