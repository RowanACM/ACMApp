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


# Response codes
RESPONSE_SUCCESSFUL_NEW = 100
RESPONSE_SUCCESSFUL_EXISTING = 110
RESPONSE_ALREADY_SIGNED_IN = 120
RESPONSE_REGISTERED = 130
RESPONSE_ATTENDANCE_DISABLED = 200
RESPONSE_INVALID_INPUT = 210
RESPONSE_UNKNOWN_ERROR = 220


def valid_input(uid, name, email):
    return uid is not None and name is not None and email is not None and \
           len(uid) > 0 and len(name) > 0 and len(email) > 0 and email.endswith("rowan.edu")


def setup_firebase():
    return ids.myfirebase


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
    onSlack = Slack.is_user_on_slack(email)

    new_member = myfirebase.get('/members/' + uid + "/email", None) != email

    myfirebase.put('/members/', uid + "/email", email)
    myfirebase.put('/members/', uid + "/username", username)
    myfirebase.put('/members/', uid + "/name", name)
    myfirebase.put('/members/', uid + "/uid", uid)
    myfirebase.put('/members/', uid + "/on_slack", onSlack)

    return new_member


def is_already_signed_in(current, myfirebase, uid):
    return myfirebase.get('/attendance/' + current + '/' + uid, None) is not None


def is_attendance_enabled(myfirebase):
    return myfirebase.get('/attendance/status/enabled', None)


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
    # It is not currently used
    # sns = boto3.client('sns')
    # number = '+18005551234'
    # sns.publish(PhoneNumber=number, Message='example text message')

    return True


def sign_in(uid, name, email):

    if not valid_input(uid, name, email):
        print("Invalid parameters")
        return RESPONSE_INVALID_INPUT

    myfirebase = setup_firebase()

    print("Step 2")

    # Register the user. Doesn't do anything if they are already registered
    new_member = register(myfirebase, name, email, uid)

    if new_member:
        email_new_member(email, myfirebase)
        # If they are already on slack, this won't do anything
        print("Inviting", name, "to slack", Slack.invite_to_slack(email=email))

    if not is_attendance_enabled(myfirebase):
        if new_member:
            return RESPONSE_REGISTERED
        return RESPONSE_ATTENDANCE_DISABLED

    # The location of the current attendance in the database
    current = myfirebase.get('/attendance/status/current', None)

    # If the member already signed in, return RESPONSE_ALREADY_SIGNED_IN
    if is_already_signed_in(current, myfirebase, uid):
        return RESPONSE_ALREADY_SIGNED_IN

    # Sign the user in to the meeting
    myfirebase.put('/attendance/', current + "/" + uid, {"uid": uid, "name": name, "email": email})
    print("You signed in")

    # Increment signed in count
    increment_signed_in_count(current, myfirebase)

    if new_member:
        print("First meeting")
        increment_new_member_count(myfirebase)

    increment_meeting_count(myfirebase, uid)

    if new_member:
        return RESPONSE_SUCCESSFUL_NEW
    return RESPONSE_SUCCESSFUL_EXISTING
