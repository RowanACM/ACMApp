import slacker
import ids


def invite_to_slack(email):
    try:
        ids.adminbot.users.admin.invite(email=email)
        return True
    # Throws an exception if the user is already on Slack
    except slacker.Error:
        return False


def is_user_on_slack(email):
    members = ids.slackbot.users.list().body["members"]
    for member in members:
        if member.get("profile").get("email") == email:
            return True
    return False
