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


def get_slack_username(email):
    members = ids.slackbot.users.list().body["members"]
    for member in members:
        if member.get("profile").get("email") == email:
            return member.get("name")
    return None


def get_slack_picture(email):
    members = ids.slackbot.users.list().body["members"]
    for member in members:
        if member.get("profile").get("email") == email:
            return member.get("profile").get("image_512")
    return None


def get_slack_user_id(email):
    members = ids.slackbot.users.list().body["members"]
    for member in members:
        if member.get("profile").get("email") == email:
            return member.get("id")
    return None


def invite_user_to_channel(channel_id, email):
    slack_user_id = get_slack_user_id(email)

    try:
        ids.adminbot.channels.invite(channel_id, slack_user_id)
    except slacker.Error as e:
        pass
