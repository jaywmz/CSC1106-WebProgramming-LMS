document.addEventListener("DOMContentLoaded", function() {
    // Fetch total approved courses
    fetch('/market/totalApprovedCourses')
        .then(response => response.json())
        .then(data => {
            document.getElementById('totalCourses').innerText = data;
        })
        .catch(error => console.error('Error fetching total courses:', error));

    // Fetch marketplace categories
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
        })
        .catch(error => console.error('Error fetching categories:', error));

    // Fetch user balance and role
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
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok ' + response.statusText);
                }
                return response.json();
            })
            .then(data => {
                document.getElementById("currentBalance").innerText = `$${data.balance.toFixed(2)}`;
            })
            .catch(error => console.error('Error fetching balance:', error));

        fetch(`/user/${userId}/role`)
            .then(response => response.json())
            .then(data => {
                console.log('Fetched role:', data.role);
                if (data.role.toLowerCase() !== 'professor') {
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
            .catch(error => console.error('Error fetching user role:', error));
    }

    function showModal(message) {
        const modal = document.getElementById('errorModal');
        const modalMessage = document.getElementById('modalMessage');
        modalMessage.innerText = message;
        const bootstrapModal = new bootstrap.Modal(modal);
        bootstrapModal.show();
    }

    var braintreeInstance;
    var form = document.querySelector('#checkout-form');

    $('#topUpModal').on('shown.bs.modal', function () {
        // Clear previous instance if exists
        if (braintreeInstance) {
            braintreeInstance.teardown(function (teardownErr) {
                if (teardownErr) {
                    console.error('Could not teardown previous Braintree instance:', teardownErr);
                }
                createBraintreeInstance();
            });
        } else {
            createBraintreeInstance();
        }
    });

    function createBraintreeInstance() {
        fetch('/braintree/client-token')
            .then(function (response) {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.text();
            })
            .then(function (clientToken) {
                // Ensure the container is empty before initializing the Drop-in UI
                document.getElementById('dropin-container').innerHTML = '';

                braintree.dropin.create({
                    authorization: clientToken,
                    container: '#dropin-container'
                }, function (createErr, instance) {
                    if (createErr) {
                        console.error('Create Error', createErr);
                        return;
                    }

                    braintreeInstance = instance;

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
                                form.submit();
                            });
                        }
                    });

                    // Disable the "Choose another way to pay" button while processing
                    instance.on('paymentMethodRequestable', function (event) {
                        document.querySelector('.braintree-option__header').style.pointerEvents = 'none';
                        document.querySelector('.braintree-option__header').style.opacity = '0.5';
                    });

                    instance.on('noPaymentMethodRequestable', function (event) {
                        document.querySelector('.braintree-option__header').style.pointerEvents = '';
                        document.querySelector('.braintree-option__header').style.opacity = '';
                    });
                });
            })
            .catch(function (error) {
                console.error('Error:', error);
            });
    }
});
