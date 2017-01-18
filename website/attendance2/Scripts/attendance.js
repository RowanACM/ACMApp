// ACM

var public_spreadsheet_url = 'https://docs.google.com/spreadsheets/d/1cFTIl6Xy8vsSneUq8DOwUpJmGcO2atZFndHlJvli89Q/pubhtml';

var theData = null;
var signed_in_count = 0;
var new_member_count = 0;
var poll_results = {};
var poll_question = "Loading poll question"

var currentMeeting = null;
var attendanceEnabled = false;

var signedInGoogle = false;
var signedInMeeting = false;


// Initialize Firebase
var config = {
apiKey: "AIzaSyD52Hpo4RJkxLhHPcRRw9HXd-thHVYD_O0",
authDomain: "rowan-acm.firebaseapp.com",
databaseURL: "https://rowan-acm.firebaseio.com",
storageBucket: "rowan-acm.appspot.com",
messagingSenderId: "536754237521"
};
firebase.initializeApp(config);

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


function firebaseAttendanceSetup() {
	var meetingRef = firebase.database().ref('attendance/current');
	meetingRef.on('value', function(snapshot) {
  		currentMeeting = snapshot.val();
	});
	
	var enabledRef = firebase.database().ref('attendance/enabled');
	enabledRef.on('value', function(snapshot) {
  		attendanceEnabled = snapshot.val();
  		
  		if(attendanceEnabled) {
  			document.getElementById("attendance").innerHTML = "Sign in to the meeting";
  			if(signedInGoogle) {
    			showSignInButton();
    		}
  		}
  		else {
  			document.getElementById("attendance").innerHTML = "Attendance is disabled";
  		}
	});
	
	var provider = new firebase.auth.GoogleAuthProvider();
	provider.addScope('https://www.googleapis.com/auth/plus.login');

}

firebaseAttendanceSetup()

firebase.auth().onAuthStateChanged(function(user) {
  
  if (user) {
    // User is signed in.
    signedInGoogle = true;
    if(attendanceEnabled) {
    	showSignInButton();
    }
    
  } else {
    // No user is signed in.
    signedInGoogle = false;
  }
});

function googleSignIn() {
	firebase.auth().signInWithPopup(provider).then(function(result) {
	  // This gives you a Google Access Token. You can use it to access the Google API.
	  var token = result.credential.accessToken;
	  // The signed-in user info.
	  var user = result.user;
	  // ...
	}).catch(function(error) {
	  // Handle Errors here.
	  var errorCode = error.code;
	  var errorMessage = error.message;
	  // The email of the user's account used.
	  var email = error.email;
	  // The firebase.auth.AuthCredential type that was used.
	  var credential = error.credential;
	  // ...
	});
}

function showSignInButton() {
	document.getElementById("meeting_button").style.visibility = "visible";
}

function hideSignInButton() {
	document.getElementById("meeting_button").style.visibility = "hidden";
}


function firebaseTest() {
	var user = firebase.auth().currentUser;
	var name, email, photoUrl, uid, emailVerified;
	
	
	if (user != null) {
	  	name = user.displayName;
	  	email = user.email;
	  	photoUrl = user.photoURL;
	  	emailVerified = user.emailVerified;
	  	uid = user.uid; // The user's ID, unique to the Firebase project. Do NOT use
					   	// this value to authenticate with your backend server, if
					   	// you have one. Use User.getToken() instead.
					   	
		firebase.database().ref('attendance/' + currentMeeting + "/" + uid).set({
			name, uid
		});
  	
  	}
	
}
