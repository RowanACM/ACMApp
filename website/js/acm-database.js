//initialize firebase
var config = {
  apiKey: "AIzaSyD52Hpo4RJkxLhHPcRRw9HXd-thHVYD_O0",
  authDomain: "rowan-acm.firebaseapp.com",
  databaseURL: "https://rowan-acm.firebaseio.com",
  storageBucket: "rowan-acm.appspot.com",
  messagingSenderId: "536754237521"
};
firebase.initializeApp(config);

// ANNOUNCEMENTS //
var database = firebase.database();
var announcementsRef = database.ref("announcements").orderByKey().limitToLast(10);
var announcementsList = document.getElementById('announcementsList');

announcementsRef.once('value').then(function(snapshot) {
  snapshot.forEach(function(childSnapshot) {
    addPost(childSnapshot.val().title, childSnapshot.val().subj, childSnapshot.val().text, childSnapshot.val().author, childSnapshot.val().date);
  });
});

function addPost(title, subject, text, author, date) {
  //console.log(title + ' ' + subject + ' ' + text);
  announcementsList.insertAdjacentHTML('afterbegin',
  '<div class="post-preview">' +
  '    <a href="post.html">' +
  '        <h2 class="post-title">' + title + '</h2>' +
  '        <h3 class="post-subtitle">' + subject + '</h3>' +
  '    </a>' +
  '    <p class="post-meta">Posted by <a href="#">' + author + '</a> on ' + date + '</p>' +
  '</div>' +
  '<hr>');
}
