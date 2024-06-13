document.addEventListener('DOMContentLoaded', async function(){
    const courses = await getCourse();
    // const courseList = document.getElementById('course-list');
    // courses.forEach(course => {
    //     const courseItem = document.createElement('li');
    //     courseItem.textContent = course.name;
    //     courseList.appendChild(courseItem);
    // });
});

async function getCourse(){
    const response = await fetch('/courses');
    const data = await response.json();
    console.log(data);
    return data;
}