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


def get_member_info(user_id_token):
    try:
        decoded_token = auth.verify_id_token(user_id_token)
    except:
        return None
    uid = decoded_token['uid']

    member = myfirebase.get("/members/", uid)

    on_slack = Slack.is_user_on_slack(member["email"])

    t2 = time.time()
    on_github = Github.is_user_on_github(member["email"])
    print(time.time() - t2)

    name = member["name"]
    rowan_email = member["email"]

    phone_number = None

    return {"name": name, "rowan_email": rowan_email, "on_github": on_github, "on_slack": on_slack, "uid": uid}
