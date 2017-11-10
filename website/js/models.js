//Refernce for events page, no limit
var announcementsRef_all = firebase.database().ref("announcements").orderByChild("timestamp");

announcementsRef_all.once('value').then(function(snapshot) {
  snapshot.forEach(function(childSnapshot) {
    addPost(childSnapshot, "#announcementsList_all");
  });
});

//Refernce for home page, limit to 5
var announcementsRef_5 = announcementsRef_all.limitToLast(5);

announcementsRef_5.once('value').then(function(snapshot) {
  snapshot.forEach(function(childSnapshot) {
    addPost(childSnapshot, "#announcementsList_5");
  });
});

//Add a post to the list
var addPost = function(snapshot, elementId) {
  var d = new Date(Number(snapshot.val().timestamp) * 1000);

  $(elementId).prepend(
    '<div class="post-preview">' +
    ' <a href="#' + snapshot.key + '">' +
    '   <h2 class="post-title">' + snapshot.val().title + '</h2>' +
    '   <p class="post-subtitle">' + snapshot.val().snippet + '</p>' +
    '  </a>' +
    '  <p class="post-meta">Posted by ' + snapshot.val().committee + ' on ' + d.toDateString() + '</p>' +
    '</div>'
    );
};

//Used to set the post inside the postDiv
var setPost = function(snapshot) {
  var d = new Date(Number(snapshot.val().timestamp) * 1000);

  $("#postDiv").html(
    '<div class="post-preview">' +
    '  <h1>' + snapshot.val().title + '</h1>' +
    '  <p class="post-meta">Posted by ' + snapshot.val().committee + ' on ' + d.toDateString() + '</p>' +
    '  <p>' + snapshot.val().text + '</p>' +
    '</div>');
};

var switchView = function() {
  var postKey = location.hash.slice(1);
  if (postKey != "") {
    $(".aList").hide();
    firebase.database().ref('announcements/' + postKey).once('value').then(function(snapshot) {
      setPost(snapshot);
      $("#postDiv").show();
    });
  } else {
    $("#postDiv").hide();
    $(".aList").show();
  }
};

//On hash change, change page structure
$(window).on('hashchange',function(){
  switchView();
});

$(document).ready(function() {
  switchView();
});
