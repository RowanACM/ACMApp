// ACM

var signed_in_count = 0;
var new_member_count = 0;

var currentMeeting = null;
var attendanceEnabled = false;

var signedInGoogle = false;
var signedInMeeting = false;

var admin = false;

var meetingSignedInRef;


// Initialize Firebase
var config = {
	apiKey: "AIzaSyD52Hpo4RJkxLhHPcRRw9HXd-thHVYD_O0",
	authDomain: "rowan-acm.firebaseapp.com",
	databaseURL: "https://rowan-acm.firebaseio.com",
	storageBucket: "rowan-acm.appspot.com",
	messagingSenderId: "536754237521"
};
firebase.initializeApp(config);


var attendanceRef = firebase.database().ref('attendance').child("status");
attendanceRef.on('value', function(snapshot) {
	currentMeeting = snapshot.child("current").val();
	attendanceEnabled = snapshot.child("enabled").val();
	new_member_count = snapshot.child("new_member_count").val();
	signed_in_count = snapshot.child("signed_in_count").val();
	
	if(attendanceEnabled) {
		if(signedInGoogle) {
			changeViewsSignedInGoogle();
		}
		
		determineIfSignedInMeeting();
	}
	else {
		document.getElementById("attendance").innerHTML = "Sign in to your Google Account (Rowan Email Address)";
		document.getElementById("meeting_button").innerHTML = "Register for Rowan ACM";
	}
	
	
	updateSignInViews();
});



firebase.auth().onAuthStateChanged(function(user) {
	console.log("ON AUTH STATE CHANGED " + user + " SIGNED IN: " + (user != null));
	 
  	if (user) {
  		var email = user.email
  		if(email != null && email.includes("rowan.edu")) {
			// User is signed in.
			signedInGoogle = true;
			if(!signedInMeeting) {
				changeViewsSignedInGoogle()
			}
	
			if(currentMeeting != null) {	
				determineIfSignedInMeeting();
			}
			
			determineIfAdmin();
	  	}
	  	else {
	  		firebaseSignOut();
	  		alert("Please sign in with your students.rowan.edu email address");
	  		document.getElementById("attendance").innerHTML = "Please sign in with your students.rowan.edu email address";
	  	}
	} else {
		// No user is signed in.
		signedInGoogle = false;
		document.getElementById("meeting_button").style.visibility = "hidden";
		document.getElementById("sign_out").style.visibility = "hidden";
		document.getElementById("google_sign_in").style.visibility = "visible";
		document.getElementById("attendance").innerHTML = "Sign in to your Google account\n(Rowan Email Address)";
	}
});


function determineIfSignedInMeeting() {
	if(firebase.auth().currentUser != null) {
		var uid = firebase.auth().currentUser.uid;
		meetingSignedInRef = firebase.database().ref('attendance/' + currentMeeting + "/" + uid);
		meetingSignedInRef.on('value', function(snapshot) {
			if(snapshot.val() != null && snapshot.child("uid").val() === uid) { // If signed in
				console.log("ALREADY SIGNED IN");
				signedInToMeeting();
			}
		}); 
	} 
}

function signingInToMeeting() {
	console.log("SIGNING IN TO MEETING");
	document.getElementById("meeting_button").style.visibility = "hidden";
	if(attendanceEnabled)
		document.getElementById("attendance").innerHTML = "Signing in to the meeting...";
	else
		document.getElementById("attendance").innerHTML = "Registering for ACM...";
}


function signedInToMeeting() {
	console.log("SIGNED IN TO MEETING");

	signedInMeeting = true;
	document.getElementById("meeting_button").style.visibility = "hidden";
	document.getElementById("attendance").innerHTML = "You signed in to the meeting ✓";
}

function registeredForACM() {
	console.log("REGISTERED FOR ACM");

	signedInMeeting = true;
	document.getElementById("meeting_button").style.visibility = "hidden";
	document.getElementById("attendance").innerHTML = "You registered for ACM. You should have received an email with more information about the club. ✓";
}

function submitAttendance() {
	var user = firebase.auth().currentUser;
	if (user != null) {
		console.log("SIGNING IN TO MEETING");
		signingInToMeeting();
	
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
        			document.getElementById("attendance").innerHTML = "Signed in successfully ✓<br/>Welcome to your first ACM Meeting. You should have received an email with more information about the club.";
        			break;
    			case 110:
        			// Signed in successfully. Existing member
        			signedInToMeeting();
        			break;
    			case 120:
        			// Already signed in
        			signedInToMeeting();
        			break;
        		case 130:
        			// Already signed in
        			registeredForACM();
        			break;
    			case 200:
        			// Didn't sign in. Attendance disabled
        			document.getElementById("meeting_button").style.visibility = "hidden";
					document.getElementById("attendance").innerHTML = "You already registered for ACM";
        			break;
        		case 210:
        			// Invalid input
        			document.getElementById("meeting_button").style.visibility = "visible";
					document.getElementById("attendance").innerHTML = "Error signing in. Try again. ERROR MESSAGE: INVALID INPUT";
        			break;
        		case 220:
        			// Invalid input
        			document.getElementById("meeting_button").style.visibility = "visible";
					document.getElementById("attendance").innerHTML = "Error signing in. Try again. ERROR MESSAGE: UNKNOWN ERROR";
        			break;
    			default:
    				// Invalid input
        			document.getElementById("meeting_button").style.visibility = "visible";
					document.getElementById("attendance").innerHTML = "Error signing in. Try again. ERROR MESSAGE: UNKNOWN ERROR";
			}
		  },
		  error: function(xhr) {
			//Do Something to handle error
			alert("Connection Error: " + xhr);
		  }
		});
  	}
}


function updateSignInViews() {
    document.getElementById("signed_in").innerHTML = "People signed in: " + signed_in_count;
    document.getElementById("first_meeting").innerHTML = "New members: " + new_member_count;
}


function changeViewsSignedInGoogle() {
	document.getElementById("meeting_button").style.visibility = "visible";
	document.getElementById("sign_out").style.visibility = "visible";
	document.getElementById("google_sign_in").style.visibility = "hidden";
	document.getElementById("attendance").innerHTML = "Sign in to the meeting";
}

function googleSignIn() {
	var provider = new firebase.auth.GoogleAuthProvider();
	provider.addScope('https://www.googleapis.com/auth/plus.login');
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

function onSignIn(googleUser) {
  console.log('Google Auth Response', googleUser);
  // We need to register an Observer on Firebase Auth to make sure auth is initialized.
  var unsubscribe = firebase.auth().onAuthStateChanged(function(firebaseUser) {
    unsubscribe();
    // Check if we are already signed-in Firebase with the correct user.
    if (!isUserEqual(googleUser, firebaseUser)) {
      // Build Firebase credential with the Google ID token.
      var credential = firebase.auth.GoogleAuthProvider.credential(
          googleUser.getAuthResponse().id_token);
      // Sign in with credential from the Google user.
      firebase.auth().signInWithCredential(credential).catch(function(error) {
        // Handle Errors here.
        var errorCode = error.code;
        var errorMessage = error.message;
        // The email of the user's account used.
        var email = error.email;
        // The firebase.auth.AuthCredential type that was used.
        var credential = error.credential;
        // ...
      });
    } else {
      console.log('User already signed-in Firebase.');
    }
  });
}

function isUserEqual(googleUser, firebaseUser) {
  if (firebaseUser) {
    var providerData = firebaseUser.providerData;
    for (var i = 0; i < providerData.length; i++) {
      if (providerData[i].providerId === firebase.auth.GoogleAuthProvider.PROVIDER_ID &&
          providerData[i].uid === googleUser.getBasicProfile().getId()) {
        // We don't need to reauth the Firebase connection.
        return true;
      }
    }
  }
  return false;
}

// Sign out of Firebase and Google
function firebaseSignOut() {
    var auth2 = gapi.auth2.getAuthInstance();
    auth2.signOut().then(function () {
      console.log('User signed out.');
    });
    firebase.auth().signOut();
    signedInMeeting = false;
}


function determineIfAdmin() {
	if(firebase.auth().currentUser != null) {
		var uid = firebase.auth().currentUser.uid;
		adminRef = firebase.database().ref('members/' + uid + "/admin");
		adminRef.on('value', function(snapshot) {
			if(snapshot.val()) { // If signed in
				admin = true;
				console.log("USER IS ADMIN");
				showAdminViews();
			}
			else
				admin = false;
		}); 
	} 
}

function showAdminViews() {
	document.getElementById("admin_title").style.visibility = "visible";
	document.getElementById("get_attendance_button").style.visibility = "visible";
	document.getElementById("toggle_attendance_button").style.visibility = "visible";
}

function toggleAttendanceEnabled() {
	attendanceEnabled = !attendanceEnabled;
	firebase.database().ref('attendance').child("status").child("enabled").set(attendanceEnabled);
	if(attendanceEnabled)
		alert("You enabled the attendance");
	else
		alert("You disabled the attendance");
}

function download(filename, text) {
    var pom = document.createElement('a');
    pom.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
    pom.setAttribute('download', filename);

    if (document.createEvent) {
        var event = document.createEvent('MouseEvents');
        event.initEvent('click', true, true);
        pom.dispatchEvent(event);
    }
    else {
        pom.click();
    }
}

function exportAttendance() {
	var exportRef = firebase.database().ref("attendance").child(currentMeeting);
	exportRef.once('value', function(snapshot) {
		console.log("EXPORT RECEIVED");
		var result = snapshot.val()
		
		var attendanceExport = "";
		for(var member in result) {
			attendanceExport += result[member]["name"] + "," + result[member]["email"] + "\n";
		
		}
		var fileName = "attendance_" + currentMeeting + ".csv";
		download(fileName, attendanceExport);
	}); 

}
