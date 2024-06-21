
document.addEventListener('DOMContentLoaded', async function(){
    if(window.location.pathname === '/community'){
        await getPostCountHome();
    }else if(window.location.pathname === '/community/students'){
        await getPostCountStudents();
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
        document.getElementById('offTopicCount').textContent = data[3];
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
    } catch (error) {
        console.error('There was a problem with the fetch operation: ' + error.message);
    }
}