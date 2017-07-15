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


def get_user_info(email):
    members = ids.slackbot.users.list().body["members"]
    for member in members:
        if member.get("profile").get("email") == email:
            id = member.get("id")
            username = member.get("name")
            picture = member.get("profile").get("image_512")

            return id, username, picture

    return None


def invite_user_to_channel(channel_id, email):
    slack_user_id, _, _ = get_user_info(email)

    try:
        ids.adminbot.channels.invite(channel_id, slack_user_id)
    except slacker.Error as e:
        pass
