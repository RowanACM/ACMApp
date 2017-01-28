print "START IMPORTS"

import firebase

from Email import Email
from slacker import Slacker

print "START CODE"

# Response codes
RESPONSE_SUCCESSFUL_NEW = 100
RESPONSE_SUCCESSFUL_EXISTING = 110
RESPONSE_ALREADY_SIGNED_IN = 120
RESPONSE_REGISTERED = 130
RESPONSE_ATTENDANCE_DISABLED = 200
RESPONSE_INVALID_INPUT = 210
RESPONSE_UNKNOWN_ERROR = 220

FIREBASE_LICENSE_KEY = ""
SLACK_KEY = ""


def valid_input(uid, name, email):
    return len(uid) > 0 and len(name) > 0 and len(email) > 0


def setup_firebase():
    myfirebase = firebase.FirebaseApplication('https://rowan-acm.firebaseio.com/', None)
    authentication = firebase.FirebaseAuthentication(FIREBASE_LICENSE_KEY, 'tylercarberry@gmail.com', admin=True)
    myfirebase.authentication = authentication
    return myfirebase


def register(myfirebase, name, email, uid):
    """
    Update user info. Creates it if new member
    :param myfirebase:
    :param name:
    :param email:
    :param uid:
    :return:
    """
    username = email.split("@")[0]
    onSlack = on_slack(email)

    new_member = myfirebase.get('/members/' + uid + "/email", None) != email

    myfirebase.put('/members/', uid + "/email", email)
    myfirebase.put('/members/', uid + "/username", username)
    myfirebase.put('/members/', uid + "/name", name)
    myfirebase.put('/members/', uid + "/uid", uid)
    myfirebase.put('/members/', uid + "/on_slack", onSlack)

    return new_member


def already_signed_in(current, myfirebase, uid):
    return myfirebase.get('/attendance/' + current + '/' + uid, None) is not None


def increment_signed_in_count(current, myfirebase):
    signed_in_people = myfirebase.get('/attendance/' + current, None)
    signed_in_count = len(signed_in_people)
    myfirebase.put('/attendance/status', "/signed_in_count", signed_in_count)


def increment_new_member_count(myfirebase):
    increment_firebase(myfirebase, '/attendance/status/new_member_count')


def increment_meeting_count(myfirebase, uid):
    increment_firebase(myfirebase, '/members/' + uid + "/meeting_count")


def increment_firebase(myfirebase, path):
    count = myfirebase.get(path, None)
    if count is None:
        count = 0
    count += 1
    myfirebase.put("/", path, count)


def on_slack(email):
    """
    Determine if a user is on the slack
    :param email: The email address to check
    :return: Whether an account associated with the given email address is on slack
    """
    slack = Slacker(SLACK_KEY)
    members = slack.users.list()

    for member in members.body["members"]:
        if 'email' in member["profile"]:
            if member['profile']['email'].lower() == email.lower():
                return True

    return False


def email_new_member(email, myfirebase):
    """
    Send a welcome email to a new user of ACM
    :param email: Email address to send to
    :param the_subject: Subject of the email
    :param the_text: Message of the email in plain text
    :param the_html: Message of the email formatted with html
    :param from_email: The email address to send the email from
    :return:
    """
    print "Sending email to " + email

    email_enabled = myfirebase.get('/new_member_email/enabled', None)
    the_subject = myfirebase.get('/new_member_email/subject', None)
    the_text = myfirebase.get('/new_member_email/body', None)
    the_html = myfirebase.get('/new_member_email/html', None)
    # from_address = myfirebase.get('/new_member_email/from', None)
    from_address = "tyler@rowanacm.org"

    #print the_subject, the_text, from_address

    if email_enabled:
        email = Email(to=email, subject=the_subject)
        email.text(the_text)
        email.html(the_html)
        email.send(from_addr=from_address)

    # The following code could be used to send an sms
    # It is not currently used
    # sns = boto3.client('sns')
    # number = '+18005551234'
    # sns.publish(PhoneNumber=number, Message='example text message')

    return True


def attendance(event, context):
    """
    Sign a user in to the meeting
    :param event: A dict containing the user's uid, name, and email
    :param context: Information that is passed regarding the Lambda invocation
    :return: A response code corresponding to the status of the sign in. See the response codes at the top of the file
    """

    print "EVENT: ", event, context

    print "Entering attendance"
    if not "uid" in event:
        return RESPONSE_INVALID_INPUT
    if not "name" in event:
        return RESPONSE_INVALID_INPUT
    if not "email" in event:
        return RESPONSE_INVALID_INPUT

    uid = event["uid"]
    name = event["name"]
    email = event["email"]

    print "UID:", uid, "NAME:", name, "EMAIL:", email

    # TODO: Require that the email is a Rowan email address

    if not valid_input(uid, name, email):
        print "Invalid parameters"
        return RESPONSE_INVALID_INPUT

    myfirebase = setup_firebase()

    print "Step 2"

    # Register the user. Doesn't do anything if they are already registered
    new_member = register(myfirebase, name, email, uid)

    if new_member:
        email_new_member(email, myfirebase)

    # A boolean of whether the attendance is enabled
    attendance_enabled = myfirebase.get('/attendance/status/enabled', None)
    if not attendance_enabled:
        if new_member:
            return RESPONSE_REGISTERED
        return RESPONSE_ATTENDANCE_DISABLED

    # The location of the current attendance in the database
    current = myfirebase.get('/attendance/status/current', None)

    # If the member already signed in, return RESPONSE_ALREADY_SIGNED_IN
    if already_signed_in(current, myfirebase, uid):
        return RESPONSE_ALREADY_SIGNED_IN

    # Sign the user in to the meeting
    myfirebase.put('/attendance/', current + "/" + uid, {"uid": uid, "name": name, "email": email})
    print "You signed in"

    # Increment signed in count
    increment_signed_in_count(current, myfirebase)

    if new_member:
        print "First meeting"
        increment_new_member_count(myfirebase)

    increment_meeting_count(myfirebase, uid)

    if new_member:
        return RESPONSE_SUCCESSFUL_NEW
    return RESPONSE_SUCCESSFUL_EXISTING


# There is no main method since it is called from AWS Lambda. Uncomment the following line to test it
# REPLACE THE EMAIL ADDRESS WITH YOUR OWN. I have accidentally emailed John Smith a couple times, sorry john.
print attendance({"uid": "abc123", "email": "carberryt9@students.rowan.edu", "name": "Tyler Carberry"}, None)
