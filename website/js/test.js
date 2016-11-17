// Initializes Foo.
function Foo() {
  this.checkSetup();

  // Shortcuts to DOM Elements.
  this.navSelect = document.getElementById('navSelect');

  //This could be replicated for our purposes (auth)
  //this.signInButton = document.getElementById('sign-in');
  //this.signOutButton = document.getElementById('sign-out');
  //this.signInSnackbar = document.getElementById('must-signin-snackbar');

  //could use
  //this.signOutButton.addEventListener('click', this.signOut.bind(this));
  //this.signInButton.addEventListener('click', this.signIn.bind(this));


  // could be used for something entirely different in the future
  var buttonTogglingHandler = this.toggleButton.bind(this);
  this.messageInput.addEventListener('keyup', buttonTogglingHandler);
  this.messageInput.addEventListener('change', buttonTogglingHandler);

  this.initFirebase();
  this.loadMessages();
}



// Sets up shortcuts to Firebase features and initiate firebase auth.

Foo.prototype.initFirebase = function() {

  // Shortcuts to Firebase SDK features.
  this.auth = firebase.auth();
  this.database = firebase.database();
  this.storage = firebase.storage();

  // Initiates Firebase auth and listen to auth state changes. -- leave for 0.1
  //this.auth.onAuthStateChanged(this.onAuthStateChanged.bind(this));

};

// Loads chat messages history and listens for upcoming ones.

Foo.prototype.loadMessages = function() {

  // Reference to the /messages/ database path.

  this.messagesRef = this.database.ref('messages');

  // Make sure we remove all previous listeners.

  this.messagesRef.off();


  // Loads the last 12 messages and listen for new ones.

  var setMessage = function(data) {
    var val = data.val();
    this.displayMessage(data.key, val.name, val.text);
  }.bind(this);

  this.messagesRef.limitToLast(100).on('child_added', setMessage);
  this.messagesRef.limitToLast(100).on('child_changed', setMessage);
  this.messagesRef.limitToLast(100).on('child_removed', setMessage);

};



// Saves a new message on the Firebase DB.

Foo.prototype.saveMessage = function(e) {
  e.preventDefault();

  // Check that the user entered a message and is signed in.

  if (this.messageInput.value) {
    var currentUser = this.auth.currentUser;

    // Add a new message entry to the Firebase Database.

    this.messagesRef.push({
      name: 'Feech',
      text: this.messageInput.value

      //photoUrl: currentUser.photoURL || '/images/profile_placeholder.png'

    }).then(function() {

      // Clear message text field and SEND button state.

      Foo.resetMaterialTextfield(this.messageInput);
      this.toggleButton();
    }.bind(this)).catch(function(error) {
      console.error('Error writing new message to Firebase Database', error);
    });
  }
};



// Template for messages.

Foo.MESSAGE_TEMPLATE =
    '<div class="message-container">' +
      '<div class="spacing"><div class="pic"></div></div>' +
      '<div class="message"></div>' +
      '<div class="name"></div>' +
    '</div>';

// A loading image URL.

Foo.LOADING_IMAGE_URL = 'https://www.google.com/images/spin-32.gif';

// Displays a Message in the UI. -- look to remove pics and image args

Foo.prototype.displayMessage = function(key, name, text, picUrl, imageUri) {
  var div = document.getElementById(key);

  // If an element for that message does not exists yet we create it.

  if (!div) {
    var container = document.createElement('div');
    container.innerHTML = Foo.MESSAGE_TEMPLATE;
    div = container.firstChild;
    div.setAttribute('id', key);
    this.messageList.appendChild(div);
  }

  if (picUrl) {
    div.querySelector('.pic').style.backgroundImage = 'url(' + picUrl + ')';
  }

  div.querySelector('.name').textContent = name;
  var messageElement = div.querySelector('.message');

  if (text) { // If the message is text.
    messageElement.textContent = text;

    // Replace all line breaks by <br>.

    messageElement.innerHTML = messageElement.innerHTML.replace(/\n/g, '<br>');

  } else if (imageUri) { // If the message is an image.
    var image = document.createElement('img');
    image.addEventListener('load', function() {
      this.messageList.scrollTop = this.messageList.scrollHeight;
    }.bind(this));

    this.setImageUrl(imageUri, image);
    messageElement.innerHTML = '';
    messageElement.appendChild(image);

  }

  // Show the card fading-in and scroll to view the new message.

  setTimeout(function() {div.classList.add('visible')}, 1);
  this.messageList.scrollTop = this.messageList.scrollHeight;
  this.messageInput.focus();
};



// Enables or disables the submit button depending on the values of the input

// fields.

Foo.prototype.toggleButton = function() {

  if (this.messageInput.value) {
    this.submitButton.removeAttribute('disabled');

  } else {
    this.submitButton.setAttribute('disabled', 'true');
  }
};


// Resets the given MaterialTextField.

Foo.resetMaterialTextfield = function(element) {
  element.value = '';
  element.parentNode.MaterialTextfield.boundUpdateClassesHandler();
};


// Checks that the Firebase SDK has been correctly setup and configured.

Foo.prototype.checkSetup = function() {

  if (!window.firebase || !(firebase.app instanceof Function) || !window.config) {
    window.alert('You have not configured and imported the Firebase SDK. ' +
        'Make sure you go through the codelab setup instructions.');

  } else if (config.storageBucket === '') {
    window.alert('Your Firebase Storage bucket has not been enabled. Sorry about that. This is ' +
        'actually a Firebase bug that occurs rarely. ' +
        'Please go and re-generate the Firebase initialisation snippet (step 4 of the codelab) ' +
        'and make sure the storageBucket attribute is not empty. ' +
        'You may also need to visit the Storage tab and paste the name of your bucket which is ' +
        'displayed there.');
  }
};

window.onload = function() {
  window.foo = new Foo();
};
