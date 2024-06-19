// Course Modal
let courseModal = new bootstrap.Modal(document.getElementById('courseModal'));

let enrolledCourseTag = document.querySelector("#enrolled-course-tag");
let ongoingCourseTag = document.querySelector("#ongoing-course-tag");
let completedCourseTag = document.querySelector("#completed-course-tag");

let courseTable;

let userId = getCookie('lrnznth_User_ID');

document.addEventListener('DOMContentLoaded', async function(){
    await refreshCourseTable();
});

async function getCourseSubscriptionByUserId(id){
    const response = await fetch(`/course/subscription/user/${id}`);
    if (response.status != 200) {
        return [];
    }
    const data = await response.json();
    return data;
}

async function createCourseTable(){
    // Create table
    let table = document.createElement('table');
    table.id = "courseTable";

    // Create table head
    let thead = document.createElement('thead');
    let tr = document.createElement('tr');

    // Note: ID is the subscription Id 
    ['ID', 'Course Name', 'Instructor', "Status"].forEach(item =>{
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
    document.querySelector("#course-table-div").appendChild(table);

    // Initialize CourseTable
    courseTable = new simpleDatatables.DataTable(table);
}

async function refreshCourseTable(){
    try{    
        await courseTable.destroy();
        await document.querySelector("#courseTable").remove();
    }catch(e){}
    
    let courseSubscription = await getCourseSubscriptionByUserId(userId);

    if(courseSubscription.length == 0){
        displayEmptyCourseMessage(true);
        updateCourseSubscriptionTags(0, 0, 0);
        return;
    }

    displayEmptyCourseMessage(false);
    await createCourseTable();

    courseSubscription.forEach(subscription=>{
        let row = [
            subscription.courseId,
            subscription.courseName,
            subscription.courseInstructor,
            subscription.subscriptionStatus
        ]
        courseTable.rows.add(row);
    })

    courseTable.update();
    
    let tableBody = document.querySelector("#courseTable tbody");
    Array.from(tableBody.children).forEach((row, index) => {
        row.addEventListener('click', async function(){
            // console.log("Row clicked: ", courseSubscription[index]);
            await displayCourseModal(courseSubscription[index]);
        });
    });

    let enrolled = courseSubscription.length;
    let ongoing = courseSubscription.filter(subscription => subscription.subscriptionStatus == "ongoing").length;
    let completed = courseSubscription.filter(subscription => subscription.subscriptionStatus == "completed").length;
    

    await updateCourseSubscriptionTags(enrolled, ongoing, completed);
    
}

function displayEmptyCourseMessage(boolean){
    let emptyCourseMessage = document.querySelector("#empty-course-message");
    if(boolean){
        emptyCourseMessage.style.display = "block";
    } else {
        emptyCourseMessage.style.display = "none";
    }
}

function updateCourseSubscriptionTags(enrolled, ongoing, completed){
    enrolledCourseTag.textContent = enrolled;
    ongoingCourseTag.textContent = ongoing;
    completedCourseTag.textContent = completed;
}

function displayCourseModal(course){
    // Get the modal elements
    let modalTitle = document.querySelector('#courseModalTitle');
    let modalBody = document.querySelector('#courseModalBody');

    // Set the modal title to the course title
    modalTitle.textContent = course.courseName;

    // Clear the modal body
    modalBody.innerHTML = '';

    // Create an img element for the course image
    let img = document.createElement('img');
    img.src = course.courseCoverImageUrl;
    img.alt = course.courseName;
    img.width = 200; // adjust as needed

    // Add styles to center the image
    img.style.display = 'block';
    img.style.marginLeft = 'auto';
    img.style.marginRight = 'auto';


    // Add the image to the modal body
    modalBody.appendChild(img);

    // Create a p element for the course details
    let p = document.createElement('p');
    p.textContent = course.courseDescription; // replace with the actual property name

    // Add the details to the modal body
    modalBody.appendChild(p);

    let goCourseBtn = document.getElementById('modalGoCourseButton');
    goCourseBtn.onclick = async function(){
        window.location.href = `/coursepage?id=${course.courseId}`;
    }

    // Show the modal
    courseModal.show();
}

async function getCourse(id){
    const response = await fetch(`/course/details/${id}`);
    if (response.status != 200) {
        return null;
    }
    const data = await response.json();
    return data;
}
