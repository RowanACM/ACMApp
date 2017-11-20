/*
*UNDER CONSTRUCTION -- TEMP
*/

// Initialize Firebase
var config = {
apiKey: "AIzaSyD52Hpo4RJkxLhHPcRRw9HXd-thHVYD_O0",
authDomain: "rowan-acm.firebaseapp.com",
databaseURL: "https://rowan-acm.firebaseio.com",
storageBucket: "rowan-acm.appspot.com",
messagingSenderId: "536754237521"
};
firebase.initializeApp(config);

initApp = function() {

  firebase.auth().onAuthStateChanged(function(user) {
  }, function(error) {
    console.log(error);
  });
};

window.addEventListener('load', function() {
  initApp()
});
