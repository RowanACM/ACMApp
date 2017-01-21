// ACM

var public_spreadsheet_url = 'https://docs.google.com/spreadsheets/d/1cFTIl6Xy8vsSneUq8DOwUpJmGcO2atZFndHlJvli89Q/pubhtml';

var theData = null;
var signed_in_count = 0;
var new_member_count = 0;

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

function updateSignInCount() {
    document.getElementById("first_meeting").innerHTML = "Number of new members: " + new_member_count;
    document.getElementById("signed_in").innerHTML = "How many people signed into this meeting: " + signed_in_count;
}

function firebaseAttendanceSetup() {
	var attendanceRef = firebase.database().ref('attendance');
	attendanceRef.on('value', function(snapshot) {
  		currentMeeting = snapshot.child("current").val();
  		attendanceEnabled = snapshot.val();
  		
  		if(signedInGoogle) {
  			document.getElementById("attendance").innerHTML = "Sign in to the meeting";
  			if(signedInGoogle) {
    			showSignInButton();
    		}
    		
    		determineIfSignedInMeeting();
  		}
  		else {
  			document.getElementById("attendance").innerHTML = "Attendance is disabled";
  		}
  		
  		new_member_count = snapshot.child(currentMeeting).child("new_member_count").val();
  		signed_in_count = snapshot.child(currentMeeting).child("signed_in_count").val();
  		updateSignInCount();
	});
	
	var provider = new firebase.auth.GoogleAuthProvider();
	provider.addScope('https://www.googleapis.com/auth/plus.login');

}

firebaseAttendanceSetup()

firebase.auth().onAuthStateChanged(function(user) {
	console.log("ON AUTH STATE CHANGED " + user);
	 
  	if (user) {
  		// User is signed in.
		signedInGoogle = true;
		if(attendanceEnabled && !signedInMeeting) {
			showSignInButton();
		}
	
		if(currentMeeting != null) {	
			determineIfSignedInMeeting();
	  	}
	} else {
		// No user is signed in.
		signedInGoogle = false;
	}
});

var already_listening = false;
function determineIfSignedInMeeting() {
	if(already_listening) return; else already_listening = true;
	var uid = firebase.auth().currentUser.uid;
	var meetingSignedInRef = firebase.database().ref('attendance/' + currentMeeting + "/" + uid);
	meetingSignedInRef.on('value', function(snapshot) {
		if(snapshot.val() != null && snapshot.child("uid").val() === uid) { // If signed in
			console.log("ALREADY SIGNED IN");
			signedInToMeeting();
		}
	});  
}

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

function signedInToMeeting() {
	console.log("SIGNED IN TO MEETING");

	signedInMeeting = true;
	hideSignInButton();
	document.getElementById("attendance").innerHTML = "You signed in to the meeting ✓";
}


function submitAttendance() {
	var user = firebase.auth().currentUser;
	if (user != null) {
		$.get({
		  url: "https://2dvdaw7sq1.execute-api.us-east-1.amazonaws.com/prod/attendance",
		  type: "get", //send it through get method
		  data: { 
			uid: user.uid, 
			name: user.displayName, 
			email: user.email
		  },
		  success: function(response) {
		  	switch(response) {
    			case 100:
        			// Signed in successfully. New member
        			signedInToMeeting();
        			break;
    			case 110:
        			// Signed in successfully. Existing member
        			signedInToMeeting();
        			break;
    			case 120:
        			// Already signed in
        			signedInToMeeting();
        			break;
    			case 200:
        			// Didn't sign in. Attendance disabled
        			hideSignInButton();
        			break;
        		case 210:
        			// Invalid input
        			alert("Invalid input");
        			break;
        		case 220:
        			// Didn't sign in. Unknown error
        			alert("Unknown error");
        			break;
    			default:
    				// Didn't sign in. Unknown error
    				alert("Unknown error");
			}
		  },
		  error: function(xhr) {
			//Do Something to handle error
			alert("Connection Error: " + xhr);
		  }
		});
  	
  	}
	
}
