/*
*UNDER CONSTRUCTION -- TEMP
*/
// ANNOUNCEMENTS //

var announcementsRef_all = firebase.database().ref("announcements").orderByChild('timestamp');
var announcementsRef_5 = announcementsRef_all.limitToLast(5);
var announcementsList_all = document.getElementById('announcementsList_all');
var announcementsList_5 = document.getElementById('announcementsList_5');
var postDiv = document.getElementById('postDiv');






announcementsRef_5.once('value').then(function(snapshot) {
  snapshot.forEach(function(childSnapshot) {
    addPost(childSnapshot.val().title, childSnapshot.val().subj, childSnapshot.val().text, childSnapshot.val().author, childSnapshot.val().date, childSnapshot.val().postID, announcementsList_5);
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
  '    <a style="cursor: pointer;" onClick="getPost(\'' + postID + '\')">' +
  '        <h2 class="post-title">' + title + '</h2></a>' +
  '        <p class="post-subtitle">' + subject + '</p>' +
  '    ' +
  '    <p class="post-meta">Posted by <a href="#">' + author + '</a> on ' + date + '</p>' +
  '</div>' +
  '<hr>');
};

var setPost = function(title, subj, text, author, date){
  postDiv.innerHTML = '<div class="post-preview">' +
  '<h1>' + title + '</h1>' +
  '<p class="post-meta">Posted by <a href="#">' + author + '</a> on ' + date + '</p>' +
  '<p>' + text + '</p>';
};

var getPost = function(postID){
  $('#announcementsList_5').hide();

  firebase.database().ref('announcements/' + postID).once('value').then(function(snapshot) {
  var postTitle = snapshot.val().title;
  var postSubj = snapshot.val().subj;
  var postText = snapshot.val().text;
  var postAuthor = snapshot.val().author;
  var postDate = snapshot.val().date;
  setPost(postTitle, postSubj, postText, postAuthor, postDate);
  });
};
