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

    dataRef.push({
      title: title,
      subj: subject,
      text: text,
      committee: committee,
      timestamp: time
    }, function(error) {
      if (error){
        showError("You don't have permission to post an announcement.");
      }
      else {
        showSuccess("You successfully posted an announcment.");
        $('#title').val('');
        $("#subject").val('');
        $("#text").val('');
        $("#committee").val('General');
      }
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
