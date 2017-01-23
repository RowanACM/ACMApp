// Initialize Firebase
var config = {
    apiKey: "AIzaSyAQCEhRC7wf8iCyEW1puwteuWMP5EP7U14",
    authDomain: "acm-attendance.firebaseapp.com",
    databaseURL: "https://acm-attendance.firebaseio.com",
    storageBucket: "acm-attendance.appspot.com",
};
firebase.initializeApp(config);

// Get the number of people signed into the meeting
var signed_in_ref = firebase.database().ref('signed_in_count/');
signed_in_ref.on('value', function(snapshot) {
    var signed_in_value = snapshot.val();
    document.getElementById("signed_in").innerHTML = "How many people signed into this meeting: " + signed_in_value;
});

// Get the number of new members
var new_members_ref = firebase.database().ref('new_members_count/');
new_members_ref.on('value', function(snapshot) {
    var first_value = snapshot.val();
    document.getElementById("first_meeting").innerHTML = "Number of new members: " + first_value;
});

// Get the short url (goo.gl) of the Google Form
// var poll_url_ref = firebase.database().ref('poll_url/');
// poll_url_ref.on('value', function(snapshot) {
//     var poll_url = snapshot.val();
//     document.getElementById("poll_url").innerHTML = poll_url;
//     $("#poll_qr").attr("src", poll_url+".qr");
//     $("#poll_url").attr("href", poll_url);
//     
// });

var poll_question = "Loading poll question...";
var poll_results = null;

// Poll Question
var number = firebase.database().ref('poll_question');
number.on('value', function(snapshot) {
    poll_question = snapshot.val();
    drawChartPoll();
});

// Poll Results
var number = firebase.database().ref('poll');
number.on('value', function(snapshot) {
    poll_results = snapshot.val();
    console.log(poll_results);
    drawChartPoll();
});

// Draw the pie chart with the results of the poll
function drawChartPoll() {
    if (poll_results != null) {
        var results_array = [[poll_question, 'Votes']];
        
        for (var key in poll_results) {
            if (poll_results.hasOwnProperty(key)) {
                var name = poll_results[key].name;
                var votes = poll_results[key].votes;
                
                // Add the vote name and count to the array
                // Don't include it if nobody has voted for it
                if(votes > 0)
                    results_array.push([name, votes]);
            }
        }

        var data = google.visualization.arrayToDataTable(results_array);
        
        // Include the poll title in the pie chart
        var options = {
          title: poll_question
        };

        document.getElementById("piechart").innerHTML = "";
        var chart = new google.visualization.PieChart(document.getElementById('piechart'));
        chart.draw(data, options);
    }
}

// When the page has loaded, draw the piechart
// This is needed incase Firebase has responded before the page has fully loaded
$(window).load(function() {
    drawChartPoll();
});
