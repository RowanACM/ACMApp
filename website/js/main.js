// Initialize Firebase
var config = {
apiKey: "AIzaSyD52Hpo4RJkxLhHPcRRw9HXd-thHVYD_O0",
authDomain: "rowan-acm.firebaseapp.com",
databaseURL: "https://rowan-acm.firebaseio.com",
storageBucket: "rowan-acm.appspot.com",
messagingSenderId: "536754237521"
};
firebase.initializeApp(config);

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


      //document.getElementById('navSelect').innerHTML = '<ul class="nav navbar-nav navbar-right"><li><a href="index.html">Home</a></li><li><a href="login.html">Login</a></li><li><a href="register.html">Register</a></li></ul>';
      /*
      // Nav bar
      document.getElementById('nav').innerHTML =
            '<div class="container-fluid">' +
            '<!-- Brand and toggle get grouped for better mobile display -->' +
            '<div class="navbar-header page-scroll">' +
            '<button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">' +
            '<span class="sr-only">Toggle navigation</span>' +
            'Menu <i class="fa fa-bars"></i>' +
            '</button>' +
            '<a class="navbar-brand" href="index.html">Rowan ACM</a>' +
            '</div>' +

            '<!-- Collect the nav links, forms, and other content for toggling -->' +
            '<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">' +
            '<ul class="nav navbar-nav navbar-right">' +
            '<li>' +
              '<a href="index.html">Home</a>' +
            '</li>' +
            '</ul>' +
            '</div>' +
            '<!-- /.navbar-collapse -->' +
            '</div>' +
            '<!-- /.container -->');*/

    } else {
      // User is signed out.
      document.getElementById('sign-in-status').textContent = 'Signed out';
      document.getElementById('sign-in').textContent = 'Sign in';
      document.getElementById('account-details').textContent = 'null';

      /*
      document.getElementById('nav').innerHTML =
          '<div class="container-fluid">' +
          '<!-- Brand and toggle get grouped for better mobile display -->' +
          '<div class="navbar-header page-scroll">' +
          '<button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">' +
          '<span class="sr-only">Toggle navigation</span>' +
          'Menu <i class="fa fa-bars"></i>' +
          '</button>' +
          '<a class="navbar-brand" href="index.html">Rowan ACM</a>' +
          '</div>' +

          '<!-- Collect the nav links, forms, and other content for toggling -->' +
          '<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">' +
          '<ul class="nav navbar-nav navbar-right">' +
          '<li>' +
            '<a href="index.html">Home</a>' +
          '</li>' +
          '<li>' +
          '<a href="login.html">Login</a>' +
          '</li>' +
          '<li>' +
          '<a href="register.html">Register</a>' +
          '</li>' +
          '</ul>' +
          '</div>' +
          '<!-- /.navbar-collapse -->' +
          '</div>' +
          '<!-- /.container -->'); */
    }
  }, function(error) {
    console.log(error);
  });

  // the code above doesn't work. attempt 2: --(not working)

};

window.addEventListener('load', function() {
  initApp()
});


validateLogin = function(){
  /*
  * This function will demonstrate email validation. Until it's proven firebase
  * can hadle all of this, we will do this manually.
  */
  var email = document.getElementById('Email').value;
}
