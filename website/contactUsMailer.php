
<?php
	/* Set e-mail recipient */
	$myemail = "bhaktaj7@students.rowan.edu";

	// set CC and BCC recipients
	// $emailForCC = "bayrunsp9@students.rowan.edu";
	// $emailForBcc = "";

	/* Check all form inputs using check_input function */
	$name = check_input($_POST['name'], "Enter your name");
	$subject = check_input($_POST['subject'], "Enter a subject");
	$email = check_input($_POST['email']);
	$message = check_input($_POST['message'], "Write your message");
	$userIpAddress = $_SERVER["REMOTE_ADDR"];

	/* If e-mail is not valid show error message */
	if (!preg_match("/([\w\-]+\@[\w\-]+\.[\w\-]+)/", $email))
	{
		show_error("E-mail address not valid");
	}

	// get the 'From' part of email
	$emailHeaders = "From: $email . \r\n";
	// for CC
	// $emailHeaders .= "Cc: $emailForCC . \r\n";
	// for BCC
	// $emailHeaders .= "Bcc: $emailForBcc . \r\n";

	/* Let's prepare the message for the e-mail */
	$message = "

	IP Address recorded: $userIpAddress
	Name: $name
	E-mail: $email
	Subject: $subject

	Message:
	$message

	";

	/* Send the message using mail() function */
	mail($myemail, $subject, $message, $emailHeaders);

	/* Redirect visitor to the thank you page */
	header('Location: thanks.html');
	exit();

	/* Functions we used */
	function check_input($data, $problem='')
	{
		$data = trim($data);
		$data = stripslashes($data);
		$data = htmlspecialchars($data);
		if ($problem && strlen($data) == 0)
		{
			show_error($problem);
		}
		return $data;
	}

	function show_error($myError)
	{
?>
		<html>
		<body>

		<p>Please correct the following error:</p>
		<strong><?php echo $myError; ?></strong>
		<p>Hit the back button and try again</p>

		</body>
		</html>
	
<?php
		exit();
	}
?>
