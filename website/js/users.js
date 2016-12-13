/*
*The idea is to manage data and the site using object oriented design. This
*file represents what will become the user object and all the relative functions.
*/


// FirebaseUI config.
var uiConfig = {
  'signInSuccessUrl': '<url-to-redirect-to-on-success>',
  'signInOptions': [
    // Leave the lines as is for the providers you want to offer your users.
    firebase.auth.GoogleAuthProvider.PROVIDER_ID,
    firebase.auth.GithubAuthProvider.PROVIDER_ID,
    firebase.auth.EmailAuthProvider.PROVIDER_ID
  ],
  // Terms of service url.
  'tosUrl': '<your-tos-url>',
};


// Initialize the FirebaseUI Widget using Firebase.
var ui = new firebaseui.auth.AuthUI(firebase.auth());
// The start method will wait until the DOM is loaded.
ui.start('#firebaseui-auth-container', uiConfig);

initApp = function() {
  firebase.auth().onAuthStateChanged(function(user) {
    if (user) {
      window.location = "index.html";
        // User is signed in.
        var displayName = user.displayName;
        var email = user.email;
        var emailVerified = user.emailVerified;
        var photoURL = user.photoURL;
        var uid = user.uid;
        var providerData = user.providerData;
        user.getToken().then(function(accessToken) {
          document.getElementById('sign-in-status').textContent = 'Signed in';
          document.getElementById('sign-in').textContent = 'Sign out';
          document.getElementById('account-details').textContent = JSON.stringify({

            displayName: displayName,
            email: email,
            emailVerified: emailVerified,
            photoURL: photoURL,
            uid: uid,
            accessToken: accessToken,
            providerData: providerData
          }, null, '  ');
        });
    }
  }, function(error) {
    console.log(error);
  });
};

var register = function() {
  var firstname = $("#firstname").val();
  var lastname = $("#lastname").val();
  var email = $("#email").val();
  var username = $("#username").val();
  var pass1 = $("#pass1").val();
  var pass2 = $("#pass2").val();

  if (firstname == "") {
    showError("First name is empty.");
  } else if (lastname == "") {
    showError("Last name is empty.");
  } else if (email == "") {
    showError("Email is empty.");
  } else if (username == "") {
    showError("Username is empty.");
  } else if (pass1 == "" || pass2 == "") {
    showError("Password is empty.");
  } else if (pass1 != pass2) {
    showError("Passwords do no match.");
  } else {
    firebase.auth().createUserWithEmailAndPassword(email, pass1).catch(function(error) {
      // Handle Errors here.
      var errorCode = error.code;
      var errorMessage = error.message;
      showError(errorMessage);
    });
    $("#prob-alert").hide();
  }
};

var showError = function(message) {
  $("#prob-alert").html(message);
  $("#prob-alert").show();
};


window.addEventListener('load', function() {
  initApp()
});
