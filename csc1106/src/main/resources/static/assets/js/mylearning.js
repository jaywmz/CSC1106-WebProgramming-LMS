// Course Modal
let courseModal = new bootstrap.Modal(document.getElementById('courseModal'));

let enrolledCourseTag = document.querySelector("#enrolled-course-tag");
let ongoingCourseTag = document.querySelector("#ongoing-course-tag");
let completedCourseTag = document.querySelector("#completed-course-tag");

let courseTable;
let targetCourse;
let courseSubscription;

document.addEventListener('DOMContentLoaded', async function(){
    const courses = await getCourse();
    const courseSubscription = await getCourseSubscriptionByUserId(1);

    console.log(courses);
    console.log(courseSubscription);

    await refreshCourseTable();
});

async function getCourse(){
    const response = await fetch('/courses');
    const data = await response.json();
    if (data.error) {
        return [];
    }
    return data;
}

async function getCourseSubscriptionByUserId(id){
    const response = await fetch(`/courses/subscription/user/${id}`);
    const data = await response.json();
    if (data.error) {
        return [];
    }
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
    
    await createCourseTable();
}

function displayEmptyCourseMessage(boolean){
    let emptyCourseMessage = document.querySelector("#empty-course-message");
    let noRecentActivityMessage = document.querySelector("#no-recent-activity");
    if(boolean){
        emptyCourseMessage.style.display = "block";
        noRecentActivityMessage.style.display = "block";
    } else {
        emptyCourseMessage.style.display = "none";
        noRecentActivityMessage.style.display = "none";
    }
}

function updateCourseSubscriptionTags(enrolled, ongoing, completed){
    enrolledCourseTag.textContent = enrolled;
    ongoingCourseTag.textContent = ongoing;
    completedCourseTag.textContent = completed;
}