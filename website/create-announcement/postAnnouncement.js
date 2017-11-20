var post = function() {
  var title = document.getElementById("title").value;
  var text = document.getElementById("text").value
  var committee = document.getElementById("committee").value
  var token = document.getElementById("token").value
  var slack = document.getElementById("slack").checked

  if (title == "") {
    showError("Title is empty.");
  } else if (text == "") {
    showError("Text is empty.");
  } else if (token == "") {
    showError("Token is empty.");
  } else if (committee == "") {
    showError("Committee is empty.");
  } else {
    
    $.get({
                url: "https://api.rowanacm.org/prod/post-announcement",
                type: "get",
                data: {
                	title: title,
                	body: text,
                	committee: committee,
                    token: token,
                    also_post_on_slack: slack
                },
                success: function(response) {
                    console.log(response);
                    alert(response.message);
                },
                error: function(xhr) {
                    alert("Connection Error: " + xhr);
                }
            });

    
  }
};

var showError = function(message) {
  alert(message);
};

var showSuccess= function(message) {
  $("#succ-alert").html(message);
  $("#succ-alert").show();
  $("#prob-alert").hide();
};
