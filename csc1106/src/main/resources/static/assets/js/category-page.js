document.addEventListener('DOMContentLoaded', function() {
    const sortBySelect = document.getElementById('sort-by');
    const filterCategorySelect = document.getElementById('filter-category');
    const coursesContainer = document.getElementById('courses-list');
    const spinner = document.querySelector('.spinner-border');
    const loggedInUsername = document.getElementById('loggedInUsername');

    // Display user name in the top right corner
    const userName = getCookie('lrnznth_User_Name');
    if (userName) {
        loggedInUsername.textContent = userName;
    } else {
        loggedInUsername.textContent = 'Guest';
    }

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
            const data = await response.json();

            // Remove spinner
            coursesContainer.innerHTML = '';

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
            console.error(e);
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

    // Initial load
    loadCourses();

    function getCookie(name) {
        const value = `; ${document.cookie}`;
        const parts = value.split(`; ${name}=`);
        if (parts.length === 2) return parts.pop().split(';').shift();
    }
});
