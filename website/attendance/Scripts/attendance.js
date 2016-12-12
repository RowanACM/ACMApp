// ACM

var public_spreadsheet_url = 'https://docs.google.com/spreadsheets/d/1cFTIl6Xy8vsSneUq8DOwUpJmGcO2atZFndHlJvli89Q/pubhtml';

var theData = null;
var signed_in_count = 0;
var new_member_count = 0;
var poll_results = {};
var poll_question = "Loading poll question"

function init() {
    Tabletop.init( { key: public_spreadsheet_url,
                    callback: calculateResults,
                    simpleSheet: true } )
}

function calculateResults(data, tabletop) {
    theData = data;
    new_member_count = 0
    signed_in_count = 0
    poll_results = {}
    
    if(theData[0] != null) {
    	keys = Object.keys(theData[0]);
    	for(keyIndex in keys) {
    		key = keys[keyIndex];
    		//console.log(key);
    		if(key.toLowerCase().startsWith("weekly poll")) {
    			poll_question = key;
    		}
    	}
    }

    //alert("Successfully processed!")
    //console.log(data);
    
    for (personCount in data) {
        person = data[personCount];
        
        timestamp = person["Timestamp"];
        if(timestamp != null && timestamp.length > 0)
            signed_in_count += 1;
            
        
        newMember = person["Is this your first meeting of the semester?"];
        if(newMember === "Yes")
            new_member_count += 1;
            
        poll_answer = person[poll_question]
        if(poll_answer in poll_results)
            poll_results[poll_answer] += 1
        else
            poll_results[poll_answer] = 1
        
    }
    
    updateSignInCount()
    drawChartPoll()
    
    setTimeout(function() { init() }, 2000);
}

function updateSignInCount() {
    document.getElementById("first_meeting").innerHTML = "Number of new members: " + new_member_count;
    document.getElementById("signed_in").innerHTML = "How many people signed into this meeting: " + signed_in_count;
}

// Draw the pie chart with the results of the poll
function drawChartPoll() {
    if (poll_results != null) {
        var results_array = [[poll_question, 'Votes']];
        
        for (var key in poll_results) {
            if (poll_results.hasOwnProperty(key)) {
                var votes = poll_results[key];
                
                // Add the vote name and count to the array
                // Don't include it if nobody has voted for it
                if(votes > 0)
                    results_array.push([key, votes]);
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
// This is needed in case the attendance has loaded before the page
$(window).load(function() {
    init();
    drawChartPoll();
});
