// Cart Modal
let cartModal = new bootstrap.Modal(document.getElementById('cartModal'));

// Cart Footer includes total price and checkout button
let cartFooter = document.querySelector("#cart-footer");
let totalPriceTag = document.querySelector("#total-price-tag");
let checkoutBtn = document.querySelector("#checkout-button");
let loadingDiv = document.querySelector("#loading-div-hidden");
let loadingText = document.querySelector("#loading-text");

let cartTable;
let targetCartItem;

let userId = getCookie('lrnznth_User_ID');

document.addEventListener('DOMContentLoaded', async function () {
    await refreshCartTable();
});

async function createCartTable(){
    // Create table
    let table = document.createElement('table');
    table.id = "cartTable";

    // Create table head
    let thead = document.createElement('thead');
    let tr = document.createElement('tr');

    ['No.', 'Course Name', 'Instructor', 'Category', 'Price'].forEach(item =>{
        let th = document.createElement('th');
        th.textContent = item;
        tr.appendChild(th);
    })

    thead.appendChild(tr);
    table.appendChild(thead);

    // Create table body
    let tbody = document.createElement('tbody');
    table.appendChild(tbody);

    // Append table to a container
    document.querySelector("#cart-table-div").appendChild(table);

    // Initialize CartTable
    cartTable = new simpleDatatables.DataTable(table, {
        searchable: false,
        paging: false,
    });
}

async function refreshCartTable(){
    try{    
        await cartTable.destroy();
        await document.querySelector("#cartTable").remove();
    }catch(e){}

    let cartItems = await getCartItemsByUserId(userId);

    if(cartItems.length == 0){
        displayEmptyCartMessage(true);
        cartFooter.style.display = "none";
        return;
    }

    displayEmptyCartMessage(false);
    cartFooter.style.display = "block";

    // Create table
    await createCartTable();

    let itemNo = 0;
    let totalPrice = 0;
    cartItems.forEach(item => {
        let row = [
            ++itemNo,
            item.courseName,
            item.courseInstructor,
            item.courseCategory,
            `$ ${item.coursePrice}`
        ]
        totalPrice += item.coursePrice;
        cartTable.rows.add(row);
    })

    setTotalPriceTag(totalPrice);
    cartTable.update();

    // Show Checkout button
    checkoutBtn.style.display = "block";
    checkoutBtn.addEventListener('click', proceedCheckout);

    let tableBody = document.querySelector("#cartTable tbody");
    Array.from(tableBody.children).forEach((row, index) => {
        row.addEventListener('click', async function(){
            targetCartItem = cartItems[index];
            displayCartModal(targetCartItem);
        });
    });
    
}

function displayEmptyCartMessage(boolean){
    let emptyCartMessage = document.querySelector("#empty-cart-message");
    if(boolean === true){
        emptyCartMessage.style.display = "block";
    } else {
        emptyCartMessage.style.display = "none";
    }
}

async function getCartItemsByUserId(id){
    const response = await fetch(`/cartitems/user/${id}`);
    if (response.status != 200) {
        return [];
    }
    const data = await response.json();
    return data;
}

function setTotalPriceTag(price){
    totalPriceTag.textContent = `$ ${price}`;
}

function displayCartModal(cartItem){
    let modalTitle = document.querySelector("#cart-modal-title");
    let modalBody = document.querySelector("#cart-modal-body");

    modalTitle.textContent = cartItem.courseName;
    modalBody.innerHTML = `
        <p><strong>Added at:</strong> ${cartItem.addedAt}</p>
        <p><strong>Instructor:</strong> ${cartItem.courseInstructor}</p>
        <p><strong>Category:</strong> ${cartItem.courseCategory}</p>
        <p><strong>Price:</strong> $ ${cartItem.coursePrice}</p>
    `;

    document.getElementById('modaldeletebutton').addEventListener('click', async function(){
        await deleteCartItem(cartItem.id);
        cartModal.hide();
        await refreshCartTable();
    });

    // console.log("Cart Item: ", cartItem)

    cartModal.show();
}

async function deleteCartItem(id) {
    try {
        const response = await fetch(`/cartitems/delete/${id}`, {
            method: 'GET',
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        } else {
            const data = await response.text();
            // console.log(data);
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

async function proceedCheckout(){
    console.log("Proceed Checkout");
    showLoadingDiv(true);

    // Get All Cart Items and calculate total price
    setLoadingText("Processing your order...");
    let cartItems = await getCartItemsByUserId(userId);
    let totalPrice = 0;
    cartItems.forEach(item => {
        totalPrice += item.coursePrice;
    });

    // Check User Balance
    setLoadingText("Checking User Balance...");
    let userBalance = await getUserBalance(userId);
    if (userBalance < totalPrice){
        showLoadingDiv(false);
        return alert("Insufficient balance. Please top up your account first.");
    }

    // Proceed Checkout one by one
    try {
        for(let i = 0; i < cartItems.length; i++){

            setLoadingText("Processing Cart Item No. " + (i+1) + "...");
            
            // Deduct User Balance
            let deduction = await deductBalance(cartItems[i].coursePrice);
            if(deduction !== "Updated") {
                throw new Error("Unable to deduct balance");
            }

            // Add Course Subscription
            let addCourse = await addCourseSubscription(userId, cartItems[i].courseId);
            
            // Delete Cart Item in server
            let deleteItem = await deleteCartItem(cartItems[i].id);

        }
        // Redirect User to MyLearning Page
        window.location.href = "/mylearning";
    } catch (error) {
        showLoadingDiv(false);
        console.error('Error:', error);
        alert("An error occured. Please contact Admin. Error: "+ error);        
    }
    
    
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

async function getUserBalance(){
    const response = await fetch(`/users/balance/${userId}`);
    if (response.status != 200) {
        return 0;
    }
    const data = await response.json();
    return data;
}

async function addCourseSubscription(userId, courseId){
    try {
        const response = await fetch(`/coursesubscriptions/add/${userId}/${courseId}`, {
            method: 'GET',
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        } else {
            const data = await response.text();
            console.log(data);
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

async function deductBalance(deduction){
    const response = await fetch(`/users/balance/deduct/${userId}?balance=${deduction}`)
    if (response.status != 200) {
        throw new Error('Unable to deduct balance')
    }
    return response.text();
}