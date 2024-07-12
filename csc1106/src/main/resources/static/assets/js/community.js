document.addEventListener('DOMContentLoaded', async function(){
    if(window.location.pathname === '/community'){
        await getPostCountHome();
    }else if(window.location.pathname === '/community/students'){
        await getPostCountStudents();
    }else if(window.location.pathname === '/community/instructors'){
        await getPostCountInstructors();
    };
});

async function sortBySelected() {
    let selected = document.getElementById("sortSelector");
    try {
        const response = await fetch("/community/sort/" + selected.value, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        console.log(data);
    } catch (error) {
        console.error('There was a problem with the fetch operation: ' + error.message);
    }
}

async function getPostCountHome() {
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
        document.getElementById('offTopicCount').textContent = data[3];
        document.getElementById('feedbackCount').textContent = data[4];

        // console.log(data[4]);
        let lastOffTopic = data[5];
        let lastFeedback = data[6];

        if(lastOffTopic != 0) {
            let dateStr = lastOffTopic;
            let dateObj = new Date(dateStr);
            let formattedDate = dateObj.toLocaleDateString('en-GB', {
                day: '2-digit', // dd
                month: 'short' // MMM
            }) + ' ' + dateObj.toLocaleTimeString('en-GB', {
                hour: '2-digit', // hh
                minute: '2-digit', // mm
                hour12: true // a
            });
            document.getElementById('lastOffTopic').textContent = formattedDate;
        }
        else {
            document.getElementById('lastOffTopic').textContent = "No posts yet";
        }

        if(lastFeedback != 0) {
            let dateStr = lastFeedback;
            let dateObj = new Date(dateStr);
            let formattedDate = dateObj.toLocaleDateString('en-GB', {
                day: '2-digit', // dd
                month: 'short' // MMM
            }) + ' ' + dateObj.toLocaleTimeString('en-GB', {
                hour: '2-digit', // hh
                minute: '2-digit', // mm
                hour12: true // a
            });
            document.getElementById('lastFeedback').textContent = formattedDate;
        }
        else {
            document.getElementById('lastFeedback').textContent = "No posts yet";
        }

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
        document.getElementById('generalCount').textContent = data[0] + " Posts";
        document.getElementById('itSoftwareCount').textContent = data[1] + " Posts";
        document.getElementById('businessCount').textContent = data[2] + " Posts";
        document.getElementById('financeCount').textContent = data[3] + " Posts";
        document.getElementById('introductionsCount').textContent = data[4];
        document.getElementById('careersCount').textContent = data[5];
        
        if(data[6] != 0){
            const dateStr = data[6];
            const dateObj = new Date(dateStr);
            const formattedDate = dateObj.toLocaleDateString('en-GB', {
                day: '2-digit', // dd
                month: 'short' // MMM
            }) + ' ' + dateObj.toLocaleTimeString('en-GB', {
                hour: '2-digit', // hh
                minute: '2-digit', // mm
                hour12: true // a
            });
            document.getElementById('lastIntroduction').textContent = formattedDate;
        }else{
            document.getElementById('lastIntroduction').textContent = "No posts yet";
        }

        if(data[7] != 0){
            const dateStr = data[7];
            const dateObj = new Date(dateStr);
            const formattedDate = dateObj.toLocaleDateString('en-GB', {
                day: '2-digit', // dd
                month: 'short' // MMM
            }) + ' ' + dateObj.toLocaleTimeString('en-GB', {
                hour: '2-digit', // hh
                minute: '2-digit', // mm
                hour12: true // a
            });
            document.getElementById('lastCareers').textContent = formattedDate;
        }else{
            document.getElementById('lastCareers').textContent = "No posts yet";
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