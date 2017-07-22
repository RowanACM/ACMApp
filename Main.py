from __future__ import print_function
from __future__ import print_function

import json
import Attendance
import time
import Export
import Admin
import Github
import github
import ids
import ManageCommittee
import User
import os
import Annoucements


os.environ['TZ'] = 'US/Eastern'

NO_CACHE = 0
SHORT_CACHE = 2*60*60
MEDIUM_CACHE = 24*60*60
LONG_CACHE = 30*24*60*60


def main(event, context):
    print(event, context)

    if is_auto_manage_event(event):
        manage_attendance()
        response = {"status": "OK"}
        cache_length = NO_CACHE
    elif event is None or "resource" not in event:
        response = {"message": "No resource specified", "status": "ERROR"}
        cache_length = NO_CACHE
    elif event["resource"] == "/sign-in":
        response = sign_in(event, context)
        cache_length = NO_CACHE
    elif event["resource"] == "/set-committees":
        response = set_committees(event, context)
        cache_length = NO_CACHE
    elif event["resource"] == "/get-user-info":
        response = get_user_info(event, context)
        cache_length = NO_CACHE
    elif event["resource"] == "/get-announcements":
        response = get_announcements(event, context)
        cache_length = SHORT_CACHE
    elif event["resource"] == "/post-announcement":
        response = post_announcement(event, context)
        cache_length = NO_CACHE
    elif event["resource"] == "/github-sign-up":
        response = github_sign_up(event, context)
        cache_length = NO_CACHE
    else:
        response = {"status": "ERROR", "message": "Resource not specified"}
        cache_length = NO_CACHE

    print("RETURNING", response)

    return {
        "isBase64Encoded": True,
        "statusCode": 200,
        "headers": {"Cache-Control": "public,max-age=" + str(cache_length) + ",only-if-cached,max-stale",
                    "Access-Control-Allow-Origin": "*",
                    "Access-Control-Allow-Methods": "*",
                    "Access-Control-Allow-Headers": "*"},
        "body": json.dumps(response)
    }


def is_auto_manage_event(event):
    if "detail-type" in event and event["detail-type"] == "Scheduled Event":
        if ids.aws_cloudwatch_id in event['resources'][0]:
            return True
    return False


def sign_in(event, context):
    params = event["queryStringParameters"]
    response_code = Attendance.sign_in(google_login_token=params["token"])

    message = Attendance.code_to_message(response_code)

    status = "OK"
    if response_code >= 200:
        status = "ERROR"

    return {"message": message, "status": status, "response_code": response_code}


def github_sign_up(event, context):
    params = event["queryStringParameters"]

    try:
        user = ids.github.get_user(params["username"])
        Github.add_to_members(user)
        return {"message": "You have been added to the Github organization. Check your email to accept the invitation.", "status": "OK"}
    except github.GithubException as e:
        return {"message": "Oh no! Something went wrong.", "status": "ERROR", "github_response": str(e)}


def set_committees(event, context):
    params = event["queryStringParameters"]

    token = params["token"]
    committees = params["committees"].split(",")

    try:
        ManageCommittee.set_user_committees(my_committees=committees, token=token)
        return {"message": "You signed up for committees " + str(committees), "status": "OK"}
    except Exception as e:
        return {"message": "An unknown error occurred " + str(e), "status": "ERROR"}


def get_user_info(event, context):
    params = event["queryStringParameters"]

    token = params["token"]

    try:
        result = User.get_member_info(user_id_token=token)
        if result is not None:
            return result
        return {"message": "The token is invalid or expired", "status": "ERROR"}
    except Exception as e:
        return {"message": "An unknown error occurred " + str(e), "status": "ERROR"}


def get_announcements(event, context):
    params = event["queryStringParameters"]

    #token = params["token"]

    try:
        result = Annoucements.get_announcements()
        if result is not None:
            return result
        return {"message": "An unknown error occurred", "status": "ERROR"}
    except Exception as e:
        return {"message": "An unknown error occurred " + str(e), "status": "ERROR"}


def post_announcement(event, context):
    params = event["queryStringParameters"]

    token = params.get("token")
    title = params.get("title")
    body = params.get("body")

    if token is None or title is None or body is None:
        return {"message": "You must include a title, body, and token", "status": "ERROR"}

    committee = params.get("committee")
    also_post_on_slack = params.get("also_post_on_slack")

    try:
        user = User.get_member_info(user_id_token=token)
        if user is None:
            return {"message": "The token is invalid or expired", "status": "ERROR"}
    except Exception as e:
        return {"message": "An unknown error occurred " + str(e), "status": "ERROR"}

    if not user["is_admin"]:
        return {"message": "You must be an admin to post announcements", "status": "ERROR"}

    name = user["name"]

    try:
        Annoucements.make_announcement(title=title, body=body, committee=committee, author=name, also_post_on_slack=also_post_on_slack)
        return {"message": "Announcement posted", "status": "OK"}
    except Exception as e:
        return {"message": "An unknown error occurred " + str(e), "status": "ERROR"}


def manage_attendance():
    day_of_week = int(time.strftime("%w")) # 0=Sunday
    hour = int(time.strftime("%H"))

    meeting_key = time.strftime("%B_%d").lower()

    if day_of_week == 5 and 13 < hour < 17:
        print("meeting time")
        Admin.set_current_meeting(meeting_key)
        Admin.enable_attendance()
    else:
        print("not meeting time")
        if Admin.is_attendance_enabled():
            Export.export_attendance()
        Admin.disable_attendance()
