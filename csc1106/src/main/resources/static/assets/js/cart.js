// Cart Modal
let cartModal = new bootstrap.Modal(document.getElementById('cartModal'));

// Cart Footer includes total price and checkout button
let cartFooter = document.querySelector("#cart-footer");
let totalPriceTag = document.querySelector("#total-price-tag");
let checkoutBtn = document.querySelector("#checkout-button");

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

    ['ID', 'Course Name', 'Instructor', 'Category', 'Price'].forEach(item =>{
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
    }

    displayEmptyCartMessage(false);
    cartFooter.style.display = "block";

    // Create table
    await createCartTable();

    let totalPrice = 0;
    /////// TO BE CONTINUE BY CHENG!!!!!!!
    
    await createCartTable();
}

function displayEmptyCartMessage(boolean){
    let emptyCartMessage = document.querySelector("#empty-cart-message");
    if(boolean){
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