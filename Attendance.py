from __future__ import print_function
from __future__ import print_function
from __future__ import print_function
from __future__ import print_function
from __future__ import print_function
from __future__ import print_function
import firebase
from Email import Email
import Slack
import ids
import User


# Response codes
RESPONSE_SUCCESSFUL_NEW = 100
RESPONSE_SUCCESSFUL_EXISTING = 110
RESPONSE_ALREADY_SIGNED_IN = 120
RESPONSE_REGISTERED = 130
RESPONSE_ATTENDANCE_DISABLED = 200
RESPONSE_INVALID_INPUT = 210
RESPONSE_UNKNOWN_ERROR = 220

myfirebase = ids.myfirebase


def sign_in(google_login_token):
    user = User.get_member_info(google_login_token)

    if user is None:
        return RESPONSE_INVALID_INPUT

    is_new_member = update_user_info(user)

    # If they are already on slack this won't do anything
    Slack.invite_to_slack(email=user["rowan_email"])

    if is_new_member:
        email_new_member(user)

    if not is_attendance_enabled():
        if is_new_member:
            return RESPONSE_REGISTERED
        return RESPONSE_ATTENDANCE_DISABLED

    # The location of the current attendance in the database
    current = myfirebase.get('/attendance/status/current', None)

    # If the member already signed in, return RESPONSE_ALREADY_SIGNED_IN
    if is_already_signed_in(current, user):
        return RESPONSE_ALREADY_SIGNED_IN

    # Sign the user in to the meeting
    myfirebase.put('/attendance/', current + "/" + user["uid"], {"uid": user["uid"], "name": user["name"], "email": user["rowan_email"]})

    # Increment signed in count
    increment_signed_in_count(current)

    if is_new_member:
        increment_new_member_count()

    increment_meeting_count(user)

    if is_new_member:
        return RESPONSE_SUCCESSFUL_NEW
    return RESPONSE_SUCCESSFUL_EXISTING


def update_user_info(user):
    name = user["name"]
    email = user["rowan_email"]
    uid = user["uid"]

    username = email.split("@")[0]
    on_slack = Slack.is_user_on_slack(email)

    is_member_new = myfirebase.get('/members/' + uid + "/email", None) != email

    myfirebase.put('/members/', uid + "/email", email)
    myfirebase.put('/members/', uid + "/username", username)
    myfirebase.put('/members/', uid + "/name", name)
    myfirebase.put('/members/', uid + "/uid", uid)
    myfirebase.put('/members/', uid + "/on_slack", on_slack)

    return is_member_new


def is_already_signed_in(current, user):
    return myfirebase.get('/attendance/' + current + '/' + user["uid"], None) is not None


def is_attendance_enabled():
    return myfirebase.get('/attendance/status/enabled', None)


def increment_signed_in_count(current):
    signed_in_people = myfirebase.get('/attendance/' + current, None)
    signed_in_count = len(signed_in_people)
    myfirebase.put('/attendance/status', "/signed_in_count", signed_in_count)


def increment_new_member_count():
    increment_firebase('/attendance/status/new_member_count')


def increment_meeting_count(user):
    increment_firebase('/members/' + user["uid"] + "/meeting_count")


def increment_firebase(path):
    count = myfirebase.get(path, None)
    if count is None:
        count = 0
    count += 1
    myfirebase.put("/", path, count)


def email_new_member(user):
    email = user["rowan_email"]

    print("Sending email to " + email)

    email_enabled = myfirebase.get('/new_member_email/enabled', None)
    the_subject = myfirebase.get('/new_member_email/subject', None)
    the_text = myfirebase.get('/new_member_email/body', None)
    the_html = myfirebase.get('/new_member_email/html', None)
    # from_address = myfirebase.get('/new_member_email/from', None)
    from_address = "tyler@rowanacm.org"

    # print the_subject, the_text, from_address

    if email_enabled:
        email = Email(to=email, subject=the_subject)
        email.text(the_text)
        email.html(the_html)
        email.send(from_addr=from_address)

    # The following code could be used to send an sms
    # sns = boto3.client('sns')
    # number = '+18005551234'
    # sns.publish(PhoneNumber=number, Message='example text message')


def code_to_message(code):
    if code == RESPONSE_SUCCESSFUL_NEW:
        return "New member. Welcome to ACM!"
    if code == RESPONSE_SUCCESSFUL_EXISTING:
        return "Signed in successfully"
    if code == RESPONSE_ALREADY_SIGNED_IN:
        return "Already signed in"
    if code == RESPONSE_REGISTERED:
        return "Successfully registered for ACM"
    if code == RESPONSE_ATTENDANCE_DISABLED:
        return "Attendance is disabled and you already registered for ACM"
    if code == RESPONSE_INVALID_INPUT:
        return "Invalid input. Check that you are signing in with your rowan email and the token has not expired"
    return "Unknown error"

