document.addEventListener('DOMContentLoaded', async function(){
    const courses = await getCourse();
    const courseSubscription = await getCourseSubscriptionByUserId(1);

    console.log(courses);
    console.log(courseSubscription);

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

