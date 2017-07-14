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

    slack_username = Slack.get_slack_username(member["email"])
    slack_picture = Slack.get_slack_picture(member["email"])

    github_username = myfirebase.get("members/" + uid, "github-username")
    if github_username is None:
        github_username = Github.get_github_username(member["email"])
        myfirebase.put("members/" + uid, "github-username", github_username)

    is_eboard = myfirebase.get("members/" + uid, "committees/eboard") is True
    is_admin = myfirebase.get("members/" + uid, "admin") is True

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
            "uid": uid}
