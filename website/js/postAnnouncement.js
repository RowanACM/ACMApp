var dataRef = firebase.database().ref("announcements");

if (!Date.now) {
  Date.now = function now() {
    return new Date().getTime();
  };
}

var post = function() {
  var title = $("#title").val();
  var subject = $("#subject").val();
  var text = $("#text").val();
  var committee = $("#committee").val();

  if (title == "") {
    showError("Title is empty.");
  } else if (subject == "") {
    showError("Subject is empty.");
  } else if (text == "") {
    showError("Text is empty.");
  } else if (committee == "") {
    showError("Committee is empty.");
  } else {
    var time = Date.now() / 1000;
      firebase.auth().currentUser.getToken( /* forceRefresh */ true).then(function(idToken) {
          var theUrl = "https://api.rowanacm.org/prod/post-announcement?token=" +idToken +"&title="+title+"&body="+text+"&committee="+committee
          alert(theUrl);

          function reqListener () {
                  console.log(this.responseText);
                  showSuccess(this.responseText);
                  $('#title').val('');
                  $("#subject").val('');
                  $("#text").val('');
                  $("#committee").val('General');
          };

          var oReq = new XMLHttpRequest();
          oReq.addEventListener("load", reqListener);
          oReq.open("GET", theUrl);
          oReq.send();

      });

  }
};

var showError = function(message) {
  $("#prob-alert").html(message);
  $("#prob-alert").show();
};

var showSuccess= function(message) {
  $("#succ-alert").html(message);
  $("#succ-alert").show();
  $("#prob-alert").hide();
};
