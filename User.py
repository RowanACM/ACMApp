import firebase_admin
from firebase_admin import credentials
from firebase_admin import auth

import ids
import Slack
import Github
import time

myfirebase = ids.myfirebase

cred = credentials.Certificate('rowan-acm-firebase-adminsdk-ahv7o-17efd6aa30.json')
default_app = firebase_admin.initialize_app(cred)


def get_member_info(user_id_token=None, uid=None):
    if user_id_token is None and uid is None:
        return None

    if uid is None:
        try:
            decoded_token = auth.verify_id_token(user_id_token)
        except:
            return None
        uid = decoded_token['uid']

    member = myfirebase.get("/members/", uid)

    slack_id, slack_username, slack_picture = Slack.get_user_info(email=member["email"])

    github_username = myfirebase.get("members/" + uid, "github-username")
    if github_username is None:
        github_username = Github.get_github_username(member["email"])
        myfirebase.put("members/" + uid, "github-username", github_username)

    committees = myfirebase.get("members/" + uid, "committees")
    my_committees = []
    for key, value in committees.iteritems():
        if value:
            my_committees.append(key)

    committee_text = "No committee selected"
    if "eboard" in my_committees:
        committee_text = "Eboard"
    elif "ai" in my_committees:
        committee_text = "AI Committee"
    elif "app" in my_committees:
        committee_text = "App Committee"
    elif "game" in my_committees:
        committee_text = "Animation/Game Committee"
    elif "robotics" in my_committees:
        committee_text = "Robotics Committee"
    elif "web" in my_committees:
        committee_text = "Web Dev Committee"

    is_eboard = myfirebase.get("members/" + uid, "committees/eboard") is True
    is_admin = myfirebase.get("members/" + uid, "admin") is True

    meeting_count = myfirebase.get("members/" + uid, "meeting_count")
    if meeting_count is None:
        meeting_count = 0

    name = member["name"]
    rowan_email = member["email"]

    phone_number = None

    return {"name": name,
            "rowan_email": rowan_email,
            "phone_number": phone_number,
            "is_eboard": is_eboard,
            "is_admin": is_admin,
            "on_github": github_username is not None,
            "github_username": github_username,
            "on_slack": slack_username is not None,
            "slack_username": slack_username,
            "profile_picture": slack_picture,
            "meeting_count": meeting_count,
            "committee_list": my_committees,
            "committee_string": committee_text,
            "member_since": "September 2016",       # TODO
            "uid": uid}
