/*
*UNDER CONSTRUCTION -- TEMP
*/
// ANNOUNCEMENTS //

var announcementsRef_all = firebase.database().ref("announcements").orderByChild('timestamp');
var announcementsRef_10 = announcementsRef_all.limitToLast(10);
var announcementsList_all = document.getElementById('announcementsList_all');
var announcementsList_10 = document.getElementById('announcementsList_10');
var postDiv = document.getElementById('postDiv');






announcementsRef_10.once('value').then(function(snapshot) {
  snapshot.forEach(function(childSnapshot) {
    addPost(childSnapshot.val().title, childSnapshot.val().subj, childSnapshot.val().text, childSnapshot.val().author, childSnapshot.val().date, childSnapshot.val().postID, announcementsList_10);
  });
});

announcementsRef_all.once('value').then(function(snapshot) {
  snapshot.forEach(function(childSnapshot) {
    addPost(childSnapshot.val().title, childSnapshot.val().subj, childSnapshot.val().text, childSnapshot.val().author, childSnapshot.val().date, childSnapshot.val().postID, announcementsList_all);
  });
});

var addPost = function(title, subject, text, author, date, postID, announcementsList) {
  //console.log(title + ' ' + subject + ' ' + text);
  announcementsList.insertAdjacentHTML('afterbegin',
  '<div class="post-preview">' +
  '    <a href="https://interactive-announcements.firebaseapp.com/">' +
  '        <h2 class="post-title">' + title + '</h2>' +
  '        <h3 class="post-subtitle">' + subject + '</h3>' +
  '    </a>' +
  '    <p class="post-meta">Posted by <a href="#">' + author + '</a> on ' + date + '</p>' +
  '</div>' +
  '<hr>');
};

var setPost = function(title, subj, text){
  postDiv.innerHTML =
  '<p>' + title + '</p>' +
  '<p>' + subj + '</p>' +
  '<p>' + text + '</p>';
};

var getPost = function(postID){
  var postTitle;
  var postSubj
  var postText

  firebase.database().ref('announcements/' + postID).once('value').then(function(snapshot) {
  postTitle = snapshot.val().title;
  postSubj = snapshot.val().subj;
  postText = snapshot.val().text;
  });
setPost(postTitle, postSubj, postText);
};
