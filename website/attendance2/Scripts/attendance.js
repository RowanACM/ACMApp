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

function updateSignInCount() {
    document.getElementById("first_meeting").innerHTML = "Number of new members: " + new_member_count;
    document.getElementById("signed_in").innerHTML = "How many people signed into this meeting: " + signed_in_count;
}

function firebaseAttendanceSetup() {
	var meetingRef = firebase.database().ref('attendance/current');
	meetingRef.on('value', function(snapshot) {
  		currentMeeting = snapshot.val();
  		
  		var newRef = firebase.database().ref('attendance/' + currentMeeting + "/new_member_count");
		newRef.on('value', function(snapshot) {
  			new_member_count = snapshot.val();
  			updateSignInCount();
		});
	
		var signedRef = firebase.database().ref('attendance/' + currentMeeting + "/signed_in_count");
		signedRef.on('value', function(snapshot) {
  			signed_in_count = snapshot.val();
  			updateSignInCount();
		});
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
	  	//name = user.displayName;
	  	//email = user.email;
	  	photoUrl = user.photoURL;
	  	emailVerified = user.emailVerified;
	  	//uid = user.uid; // The user's ID, unique to the Firebase project. Do NOT use
					   	// this value to authenticate with your backend server, if
					   	// you have one. Use User.getToken() instead.
					   	
		//firebase.database().ref('attendance/' + currentMeeting + "/" + uid).set({
		//	name, uid
		//});
		
		$.get({
		  url: "https://2dvdaw7sq1.execute-api.us-east-1.amazonaws.com/prod/attendance",
		  type: "get", //send it through get method
		  data: { 
			uid: user.uid, 
			name: user.displayName, 
			email: user.email
		  },
		  success: function(response) {
		  	alert(response);
			//Do Something
		  },
		  error: function(xhr) {
			//Do Something to handle error
			alert(xhr);
		  }
		});
  	
  	}
	
}
