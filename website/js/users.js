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

  //this should become more dynamic, with more intuitive control/interfacing.
  firebase.auth().onAuthStateChanged(function(user) {
    if (user) {


        // User is signed in. Below are properties of the user.
        var displayName = user.displayName;
        var email = user.email;
        var emailVerified = user.emailVerified;
        var photoURL = user.photoURL;
        var uid = user.uid;
        var providerData = user.providerData;

        // this is for more user/site control
        user.getToken().then(function(accessToken) {
          document.getElementById('sign-in-status').textContent = 'Signed in';
          document.getElementById('sign-in').textContent = 'Sign out';

          // keep if needed
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

        // update navbar
        var navBar = document.getElementById("navContainer");
        navBar.innerHTML =
        '<div class="navbar-header page-scroll">' +
            '<button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">' +
                '<span class="sr-only">Toggle navigation</span>' +
                'Menu <i class="fa fa-bars"></i>' +
            '</button>' +
            '<a class="navbar-brand" href="index.html">Dashboard</a>' +
        '</div>' +
        '<div id="bs-example-navbar-collapse-1" class="collapse navbar-collapse">' +
        '<ul class="nav navbar-nav navbar-right">' +
            '<li>' +
                '<a href="index.html">Home</a>' +
            '</li>' +
            '<li>' +
                '<a href="committees.html">Committees</a>' +
            '</li>' +
            '<li>' +
                '<a href="index.html" onclick="signOut();">Sign out</a>' +
            '</li>' +
        '</ul>'+
        '</div>';
    } else {
      var navBar = document.getElementById("navContainer");
      navBar.innerHTML =
      '<div class="navbar-header page-scroll">' +
          '<button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">' +
              '<span class="sr-only">Toggle navigation</span>' +
              'Menu <i class="fa fa-bars"></i>' +
          '</button>' +
          '<a class="navbar-brand" href="register.html">Become A Member</a>' +
      '</div>' +
      '<div id="bs-example-navbar-collapse-1" class="collapse navbar-collapse">' +
          '<ul class="nav navbar-nav navbar-right">' +
              '<li>' +
                  '<a href="index.html">Home</a>' +
              '</li>' +
              '<li>' +
                '<a href="committees.html">Committees</a>' +
              '</li>' +
              '<li>' +
                  '<a href="login.html">Login</a>' +
              '</li>' +
              '<li>' +
                  '<a href="register.html">Register</a>' +
              '</li>' +
          '</ul>' +
      '</div>';
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

var signOut = function() {
  firebase.auth().signOut();

};
