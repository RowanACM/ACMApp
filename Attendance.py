print "START IMPORTS"

from pushbullet import Pushbullet
from pprint import pprint
import json
import urllib2
import requests
import firebase
import json
from pyfcm import FCMNotification
import time
import twitter
from twitter import Api
from slacker import Slacker
from Email import Email
import boto3

print "START CODE"

# Response codes
RESPONSE_SUCCESSFUL_NEW = 100
RESPONSE_SUCCESSFUL_EXISTING = 110
RESPONSE_ALREADY_SIGNED_IN = 120
RESPONSE_ATTENDANCE_DISABLED = 200
RESPONSE_INVALID_INPUT = 210
RESPONSE_UNKNOWN_ERROR = 220

FIREBASE_LICENSE_KEY = ""
SLACK_KEY = ""


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


def email_new_user(email, the_subject, the_text, the_html, from_email):
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

    email = Email(to=email, subject=the_subject)
    email.text(the_text)
    email.html(the_html)
    email.send(from_addr=from_email)

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

    if len(uid) == 0 or len(name) == 0 or len(email) == 0:
        print "Invalid parameters"
        return RESPONSE_INVALID_INPUT

    myfirebase = firebase.FirebaseApplication('https://rowan-acm.firebaseio.com/', None)

    print "Step 2"

    authentication = firebase.FirebaseAuthentication(FIREBASE_LICENSE_KEY, 'tylercarberry@gmail.com', admin=True,
                                                     extra={'id': 123})
    myfirebase.authentication = authentication
    user = authentication.get_user()

    # A boolean of whether the attendance is enabled
    attendance_enabled = myfirebase.get('/attendance/status/enabled', None)
    if not attendance_enabled:
        return RESPONSE_ATTENDANCE_DISABLED

    # The location of the current attendance in the database
    current = myfirebase.get('/attendance/status/current', None)

    # If the member already signed in, return RESPONSE_ALREADY_SIGNED_IN
    my_sign_in = myfirebase.get('/attendance/' + current + '/' + uid, None)
    if not (my_sign_in is None):
        return RESPONSE_ALREADY_SIGNED_IN

    # Sign the user in to the meeting
    myfirebase.put('/attendance/', current + "/" + uid, {"uid": uid, "name": name, "email": email})
    print "You signed in"

    # Increment the number of people signed in to the meeting
    signed_in_people = myfirebase.get('/attendance/' + current, None)

    # Since I restructured the database, the following 2 lines aren't needed.
    # But I will keep them until I update the website and app
    signed_in_people.pop("signed_in_count", None)
    signed_in_people.pop("new_member_count", None)

    # Increment signed in count
    signed_in_count = len(signed_in_people)
    myfirebase.put('/attendance/status', "/signed_in_count", signed_in_count)

    # Update user info. Creates it if new member
    username = email.split("@")[0]
    myfirebase.put('/members/', uid + "/email", email)
    myfirebase.put('/members/', uid + "/username", username)
    myfirebase.put('/members/', uid + "/name", name)
    myfirebase.put('/members/', uid + "/uid", uid)

    new_member = False
    current_member_meeting_count = myfirebase.get('/members/' + uid + "/meeting_count", None)
    if current_member_meeting_count is None:
        print "First meeting"
        new_member = True
        current_member_meeting_count = 0
        myfirebase.put('/members/', uid + "/email", email)

        # Increment new_member_count
        new_member_count = myfirebase.get('/attendance/status/new_member_count', None)
        if new_member_count is None:
            new_member_count = 0
        new_member_count += 1
        myfirebase.put('/attendance/status', 'new_member_count', new_member_count)

    current_member_meeting_count += 1
    myfirebase.put('/members/', uid + "/meeting_count", current_member_meeting_count)

    # Determine whether the user is on slack
    onSlack = on_slack(email)
    myfirebase.put('/members/', uid + "/on_slack", onSlack)

    # If this is the first time that a user has signed in to the meeting, send them a welcome email
    email_enabled = myfirebase.get('/new_member_email/enabled', None)
    if new_member and email_enabled:
        subject = myfirebase.get('/new_member_email/subject', None)
        body = myfirebase.get('/new_member_email/body', None)
        html = myfirebase.get('/new_member_email/html', None)
        # from_address = myfirebase.get('/new_member_email/from', None)
        from_address = "tyler@rowanacm.org"

        print subject, body, from_address

        email_new_user(email, the_subject=subject, the_text=body, the_html=html, from_email=from_address)

    if new_member:
        return RESPONSE_SUCCESSFUL_NEW
    return RESPONSE_SUCCESSFUL_EXISTING


# There is no main method since it is called from AWS Lambda. Uncomment the following line to test it
# REPLACE THE EMAIL ADDRESS WITH YOUR OWN. I have accidentally emailed John Smith a couple times, sorry john.
# attendance({"uid": "abc123", "email": "smithj2@students.rowan.edu", "name": "John Smith"}, None)
