
document.addEventListener('DOMContentLoaded', async function(){
    if(window.location.pathname.includes('instructors')){
        await checkUserRole()
    }

    if(window.location.pathname === '/community'){
        await getPostCountHome();
    }else if(window.location.pathname === '/community/students'){
        await getPostCountStudents();
    }else if(window.location.pathname === '/community/instructors'){
        await getPostCountInstructors();
    };
});

async function getPostCountHome(){
    try{
        const response = await fetch('/community-get-post-count', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        document.getElementById('announcementsCount').textContent = data[0] + " Posts";
        document.getElementById('studentsCount').textContent = data[1] + " Posts";
        document.getElementById('instructorsCount').textContent = data[2] + " Posts";
        document.getElementById('Off-TopicCount').textContent = data[3];
        document.getElementById('FeedbackCount').textContent = data[4];
        document.getElementById('IntroductionsCount').textContent = data[5];
        document.getElementById('CareersCount').textContent = data[6];

        // console.log(data[4]);
        let lastOffTopic = data[7];
        let lastFeedback = data[8];
        let lastIntroduction = data[9];
        let lastCareers = data[10];

        formatAndDisplayDate(lastOffTopic, 'lastOff-Topic');
        formatAndDisplayDate(lastFeedback, 'lastFeedback');
        formatAndDisplayDate(lastIntroduction, 'lastIntroductions');
        formatAndDisplayDate(lastCareers, 'lastCareers');

    } catch (error) {
        console.error('There was a problem with the fetch operation: ' + error.message);
    }
}

async function getPostCountStudents(){
    try{
        const response = await fetch('/community-get-post-count-students', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        // Iterate over each key-value pair in the data
        for (const [key, value] of Object.entries(data[0])) {
            const elementId = key + 'Count';
            const element = document.getElementById(elementId);
            element.textContent = value + " Posts";
        }

    } catch (error) {
        console.error('There was a problem with the fetch operation: ' + error.message);
    }
}

async function getPostCountInstructors(){
    try{
        const response = await fetch('/community-get-post-count-instructors', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        document.getElementById('courseHelpCount').textContent = data[0] + " Posts";
        document.getElementById('teachingCount').textContent = data[1] + " Posts";
    } catch (error) {
        console.error('There was a problem with the fetch operation: ' + error.message);
    }
}

async function checkUserRole(){
    try{
        const response = await fetch("/community-get-user-role", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.text();

        if(data == "no"){
            window.location.href = "/community/unauthorised";
        }

    } catch (error) {
        console.error('There was a problem with the fetch operation: ' + error.message);
    }
}

function formatAndDisplayDate(dateStr, elementId) {
    let textContent;
    if (dateStr != 0) {
        let dateObj = new Date(dateStr);
        let formattedDate = dateObj.toLocaleDateString('en-GB', {
            day: '2-digit', // dd
            month: 'short' // MMM
        }) + ' ' + dateObj.toLocaleTimeString('en-GB', {
            hour: '2-digit', // hh
            minute: '2-digit', // mm
            hour12: true // a
        });
        textContent = formattedDate;
    } else {
        textContent = "No posts yet";
    }
    document.getElementById(elementId).textContent = textContent;
}