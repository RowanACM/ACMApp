/*
*UNDER CONSTRUCTION -- TEMP
*/
// FirebaseUI config.
var uiConfig = {
  'signInSuccessUrl': '/index.html',
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

      var navBar = document.getElementById("navContainer");
      navBar.className = "mdl-layout mdl-js-layout mdl-layout--fixed-header"
      navBar.innerHTML =
        <!-- Always shows a header, even in smaller screens. -->

            '<header class="mdl-layout__header">'+
            '<div class="mdl-layout__header-row">'+
            navTitle() +
            '<!-- Add spacer, to align navigation to the right -->' +
        '<div class="mdl-layout-spacer"></div>'+
            '<!-- Navigation. We hide it in small screens. -->'+
        '<nav class="mdl-navigation mdl-layout--large-screen-only">'+
            navLinks()+
            '</nav> '+
      '</div>'+
                ' </header>'+
    '<div class="mdl-layout__drawer mdl-layout--small-screen-only" >'+
            navTitle()+
            '<nav class="mdl-navigation">'+
            navLinks(user)+
            '</nav>'+
           '</div>'
        ;
    // catch error
  }, function(error) {
    console.log(error);
  });
};

var navTitle = function () {
    return'<a class=" mdl-layout-title mdl-navigation__link" href="index.html">ACM</a>'
}

var navLinks = function (user) {
    return '<a class="mdl-navigation__link" href="signin/">Attendance</a>' +
        '<a class="mdl-navigation__link" href="committees.html">Committees</a>' +
        '<a class="mdl-navigation__link" href="eboard.html">Eboard</a>' +
        '<a class="mdl-navigation__link" href="https://rowanacm.slack.com/">Slack</a>' +
        '<a class="mdl-navigation__link" href="app_chooser.html">Download App</a>'+
        '<a class="mdl-navigation__link" href=" https://rowan.campuslabs.com/engage/organization/acm">ProfLink</a>'+


        signInDependantNavLinks(user != null);
}

var signInDependantNavLinks = function (signedIn) {
        if(signedIn){
            return '<a class="mdl-navigation__link" href="index.html" onclick="signOut();">Sign out</a>';
        } else {
            return '<a class="mdl-navigation__link" href="login.html">Sign in</a>';
        }


}

// login function
var login = function(){
  var email = $("#email_login").val();
  var password = $("#password_login").val();

  firebase.auth().signInWithEmailAndPassword(email, password).catch(function(error) {
    // catch error
    var errorCode = error.code;
    var errorMessage = error.message;
    showError(errorMessage);
  });
  $("#prob-alert").html("You're now logged in (testing)");
  $("#prob-alert").show();

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
    $("#prob-alert").html("You're now registered (testing)");
    $("#prob-alert").show();
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

var getUser = function() {

};
