// Add product to cart
function addProductToCart(productName, price, imageUrl) {
    var cartList = document.getElementById('cartList');
    var listItem = document.createElement('li');
    listItem.classList.add('list-group-item', 'd-flex', 'justify-content-between', 'align-items-center');
    listItem.innerHTML = `
        <div class="media">
            <img src="${imageUrl}" class="mr-3" alt="${productName}" width="50">
            <div class="media-body">
                <h6 class="my-0">${productName}</h6>
                <small class="text-muted">Brief description</small>
            </div>
        </div>
        <span class="text-muted">$${price.toFixed(2)}</span>
        <button type="button" class="btn btn-sm btn-danger" onclick="removeItem(this)">Remove</button>
    `;
    cartList.appendChild(listItem);
    updateCartItemCount();
    updateCartTotal();
}

// Load cart items from localStorage when the page loads
window.addEventListener('DOMContentLoaded', (event) => {
    const cart = JSON.parse(localStorage.getItem('cart')) || [];
    cart.forEach(item => addProductToCart(item.productName, item.price, item.imageUrl));
    updateCartItemCount();
    updateCartTotal();
});

// Update cart item count
function updateCartItemCount() {
    var cartList = document.getElementById('cartList');
    var itemCount = cartList.getElementsByTagName('li').length;
    document.getElementById('cartItemCount').textContent = itemCount;
}

// Update cart total amount
function updateCartTotal() {
    const cart = JSON.parse(localStorage.getItem('cart')) || [];
    const total = cart.reduce((sum, item) => sum + item.price, 0);
    document.getElementById('cartTotal').textContent = `$${total.toFixed(2)}`;
}

// Remove item from cart
function removeItem(button) {
    var listItem = button.parentElement;
    const productName = listItem.querySelector('h6').innerText;
    listItem.remove();
    updateCartItemCount();

    // Update localStorage
    let cart = JSON.parse(localStorage.getItem('cart')) || [];
    cart = cart.filter(item => item.productName !== productName);
    localStorage.setItem('cart', JSON.stringify(cart));
    updateCartTotal();
}

// Complete checkout and clear the cart
function completeCheckout() {
    // Perform checkout logic here

    // Clear the cart
    localStorage.removeItem('cart');
    alert('Thank you for your purchase! Your cart has been cleared.');

    // Clear the cart in the DOM
    document.getElementById('cartList').innerHTML = '';
    updateCartItemCount();
    updateCartTotal();
}

// Example starter JavaScript for disabling form submissions if there are invalid fields
(function() {
    'use strict';

    window.addEventListener('load', function() {
        // Fetch all the forms we want to apply custom Bootstrap validation styles to
        var forms = document.getElementsByClassName('needs-validation');

        // Loop over them and prevent submission
        var validation = Array.prototype.filter.call(forms, function(form) {
            form.addEventListener('submit', function(event) {
                event.preventDefault();
                if (form.checkValidity() === false) {
                    event.stopPropagation();
                } else {
                    completeCheckout();
                }
                form.classList.add('was-validated');
            }, false);
        });
    }, false);
})();
