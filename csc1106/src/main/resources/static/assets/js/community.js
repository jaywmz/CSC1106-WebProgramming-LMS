document.onreadystatechange = function() {
    if (document.readyState !== "complete") {
        document.querySelector("body").style.visibility = "hidden";
        document.querySelector("#pageLoader").style.visibility = "visible";
    } else {
        document.querySelector("#pageLoader").style.display = "none";
        document.querySelector("body").style.visibility = "visible";
    }
};

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

async function sortBySelected() {
    let filter = document.getElementById("sortSelector").value;
    if (filter == "newest") n = 2;
    else if (filter == "topLikes") n = 0;
    var table, rows, switching, i, x, y, shouldSwitch, dir, switchcount = 0;
    table = document.getElementById("postsTable");
    switching = true;
    // Set the sorting direction to ascending:
    dir = "asc";
    /* Make a loop that will continue until
    no switching has been done: */
    while (switching) {
        // Start by saying: no switching is done:
        switching = false;
        rows = table.rows;
        /* Loop through all table rows (except the
        first, which contains table headers): */
        for (i = 1; i < (rows.length - 1); i++) {
            // Start by saying there should be no switching:
            shouldSwitch = false;
            /* Get the two elements you want to compare,
            one from current row and one from the next: */
            x = rows[i].getElementsByTagName("TD")[n];
            y = rows[i + 1].getElementsByTagName("TD")[n];
            /* Check if the two rows should switch place,
            based on the direction, asc or desc: */
            if (dir == "asc") {
                if (x.innerHTML.toLowerCase() > y.innerHTML.toLowerCase()) {
                    // If so, mark as a switch and break the loop:
                    shouldSwitch = true;
                    break;
                }
            } else if (dir == "desc") {
                if (x.innerHTML.toLowerCase() < y.innerHTML.toLowerCase()) {
                    // If so, mark as a switch and break the loop:
                    shouldSwitch = true;
                    break;
                }
            }
        }
        if (shouldSwitch) {
            /* If a switch has been marked, make the switch
            and mark that a switch has been done: */
            rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
            switching = true;
            // Each time a switch is done, increase this count by 1:
            switchcount ++;
        } else {
            /* If no switching has been done AND the direction is "asc",
            set the direction to "desc" and run the while loop again. */
            if (switchcount == 0 && dir == "asc") {
                dir = "desc";
                switching = true;
            }
        }
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