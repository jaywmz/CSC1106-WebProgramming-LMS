document.addEventListener('DOMContentLoaded', function() {
    const sortBySelect = document.getElementById('sort-by');
    const filterCategorySelect = document.getElementById('filter-category');
    const coursesContainer = document.getElementById('courses-list');
    const spinner = document.querySelector('.spinner-border');
    const categoryTitle = document.getElementById('category-title');
    const categoryDescription = document.getElementById('category-description');
    const loggedInUsername = document.getElementById('loggedInUsername');
    const uploadCourseLink = document.querySelector('.nav-item a[href="/upload"]');
    const viewCoursesLink = document.querySelector('.nav-item a[href="/coursesupload"]');
    const breadcrumbCategoryName = document.getElementById('breadcrumb-category-name');

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
            const sortBy = sortBySelect.value;
            const categoryId = filterCategorySelect.value || window.location.pathname.split("/").pop();
    
            if (spinner) {
                spinner.style.display = 'inline-block';
            }
    
            coursesContainer.innerHTML = '';
            coursesContainer.appendChild(spinner);
    
            // Fetch category data
            const categoryResponse = await fetch(`/api/category/${categoryId}`, { cache: "no-store" });
            if (!categoryResponse.ok) {
                throw new Error('Network response was not ok ' + categoryResponse.statusText);
            }
            const categoryData = await categoryResponse.json();
            categoryTitle.textContent = categoryData.name;
            categoryDescription.textContent = categoryData.description;
            document.getElementById('breadcrumb-category').textContent = categoryData.name; // Update breadcrumb
    
            // Fetch courses data
            const response = await fetch(`/category/${categoryId}/courses?sortBy=${sortBy}`, { cache: "no-store" });
            if (!response.ok) {
                throw new Error('Network response was not ok ' + response.statusText);
            }
    
            const data = await response.json();
            console.log('Fetched data:', data);
    
            coursesContainer.innerHTML = '';
    
            if (!Array.isArray(data)) {
                console.error("Data is not an array: ", data);
                return;
            }
    
            data.forEach(course => {
                let courseElement = document.createElement('div');
                courseElement.classList.add('col-md-4', 'mb-4');
                courseElement.innerHTML = `
                    <a href="/coursepage?id=${course.id}" style="text-decoration:none;color:inherit;" target="_blank">
                        <div class="card card-custom">
                            <img src="${course.coverImageUrl}" class="card-img-top" alt="Course Image">
                            <div class="card-body">
                                <h5 class="card-title">${course.title}</h5>
                                <p class="card-text">${course.description}</p>
                                <div class="course-rating">
                                    <span class="star">&#9733;</span>
                                    <a href="#" class="reviews review-link" data-course-id="${course.id}">${course.averageRating.toFixed(1)} (${course.reviewCount} reviews)</a>
                                </div>
                                <p class="card-price"><strong>$${course.price}</strong></p>
                                <button class="btn btn-primary add-to-cart" data-course-id="${course.id}">Add to Cart</button>
                            </div>
                        </div>
                    </a>
                `;
                coursesContainer.appendChild(courseElement);
            });
    
            if (spinner) {
                spinner.style.display = 'none';
            }
    
            addCartEventListeners();
            addReviewEventListeners();
        } catch(e) {
            console.error('Error loading courses:', e);
            if (spinner) {
                spinner.style.display = 'none';
            }
        }
    }

    function addReviewEventListeners() {
        const reviewLinks = document.querySelectorAll('.review-link');
        reviewLinks.forEach(link => {
            link.addEventListener('click', async function(event) {
                event.preventDefault();
                const courseId = event.target.getAttribute('data-course-id');
                let userId = getCookie('lrnznth_User_ID');

                if (!userId) {
                    alert('Please login to review courses');
                    return;
                }

                fetch(`/coursesubscriptions/check/${userId}/${courseId}`)
                    .then(async response => {
                        document.getElementById('reviewCourseId').value = courseId;
                        const bootstrapModal = new bootstrap.Modal(document.getElementById('reviewModal'));
                        bootstrapModal.show();
                        if(response.status !== 200){
                            document.getElementById('reviewFormMessage').textContent = 'You need to subscribe to this course to leave a review';
                            document.getElementById('submitReviewButton').disabled = true;
                        } else {
                            document.getElementById('reviewFormMessage').textContent = '';
                            document.getElementById('submitReviewButton').disabled = false;
                        }

                        // Fetch and display reviews
                        const reviewsResponse = await fetch(`/courses/${courseId}/reviews`, { cache: "no-store" });
                        const reviews = await reviewsResponse.json();
                        displayReviews(reviews);

                    })
                    .catch(error => {
                        console.error('Error checking subscription:', error);
                    });
            });
        });
    }

    function displayReviews(reviews) {
        const reviewsContainer = document.getElementById('reviewsContainer');
        reviewsContainer.innerHTML = ''; // Clear previous reviews
        reviews.forEach(review => {
            const reviewElement = document.createElement('div');
            reviewElement.classList.add('review');
            reviewElement.innerHTML = `
                <p><strong>Username:</strong> ${review.userName}</p>
                <p><strong>Rating:</strong> ${review.score}</p>
                <p><strong>Comment:</strong> ${review.comment}</p>
                <p><strong>Date:</strong> ${new Date(review.timestamp).toLocaleString('en-SG')}</p>
            `;
            reviewsContainer.appendChild(reviewElement);
        });
    }

    document.getElementById('reviewForm').addEventListener('submit', async function(event) {
        event.preventDefault();
        const courseId = document.getElementById('reviewCourseId').value;
        const rating = document.getElementById('reviewRating').value;
        const comment = document.getElementById('reviewComment').value;
        let userId = getCookie('lrnznth_User_ID');
    
        const submitButton = document.getElementById('submitReviewButton');
        submitButton.disabled = true;
        submitButton.innerText = 'Submitting...';
    
        try {
            const response = await fetch(`/courses/${courseId}/review`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: new URLSearchParams({
                    userId: userId,
                    rating: rating,
                    comment: comment
                })
            });
    
            const data = await response.text();
            if (data === "Review submitted successfully.") {
                alert('Review submitted successfully');
                
                // Fetch and display updated reviews
                const reviewsResponse = await fetch(`/courses/${courseId}/reviews`, { cache: "no-store" });
                const reviews = await reviewsResponse.json();
                displayReviews(reviews);

                const bootstrapModal = bootstrap.Modal.getInstance(document.getElementById('reviewModal'));
                bootstrapModal.hide();
                
                // Clear form fields
                document.getElementById('reviewRating').value = '';
                document.getElementById('reviewComment').value = '';
                
                // Refresh the courses list to update the displayed rating and review count
                loadCourses(); // Add a slight delay before calling loadCourses
            } else {
                alert('Failed to submit review');
            }
        } catch (error) {
            console.error('Error submitting review:', error);
        } finally {
            submitButton.disabled = false;
            submitButton.innerText = 'Submit Review';
        }
    });

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

                const subscriptionCheck = await fetch(`/coursesubscriptions/check/${userId}/${courseId}`);

                if(subscriptionCheck.status === 200){
                    alert('You have already subscribed to this course');
                    return;
                }

                const cartCheck = await fetch(`/cartitems/check/${userId}/${courseId}`);
                const cartCheckData = await cartCheck.text();
                if(cartCheckData === 'In cart'){
                    alert('Course is already in your cart');
                    return;
                }

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
                console.log('Fetched role:', data.role);
                if (data.role.toLowerCase() !== 'professor' && data.role.toLowerCase() !== 'instructor') {
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

        fetch('/braintree/client-token')
            .then(function (response) {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.text();
            })
            .then(function (clientToken) {
                document.getElementById('dropin-container').innerHTML = '';

                braintree.dropin.create({
                    authorization: clientToken,
                    container: '#dropin-container'
                }, function (createErr, instance) {
                    if (createErr) {
                        console.error('Create Error', createErr);
                        return;
                    }

                    document.querySelector('#submit-button').addEventListener('click', function (event) {
                        event.preventDefault();

                        if (confirm('Do you want to proceed with the payment?')) {
                            document.querySelector('#submit-button').disabled = true;
                            document.querySelector('#submit-button').innerText = 'Processing...';
                            document.querySelector('.braintree-dropin').style.pointerEvents = 'none';
                            document.querySelector('.braintree-dropin').style.opacity = '0.5';

                            instance.requestPaymentMethod(function (err, payload) {
                                if (err) {
                                    console.error('Request Payment Method Error', err);
                                    document.querySelector('#submit-button').disabled = false;
                                    document.querySelector('#submit-button').innerText = 'Pay';
                                    document.querySelector('.braintree-dropin').style.pointerEvents = '';
                                    document.querySelector('.braintree-dropin').style.opacity = '';
                                    return;
                                }

                                document.querySelector('#payment-method-nonce').value = payload.nonce;
                                document.querySelector('#amount').value = document.querySelector('#topUpAmount').value;
                                document.querySelector('#userId').value = userId;
                                document.querySelector('#checkout-form').submit();
                            });
                        }
                    });
                });
            })
            .catch(function (error) {
                console.error('Error:', error);
            });
    }

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
});
