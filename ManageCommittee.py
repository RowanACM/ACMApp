import Github
import ids
import Slack
import User

app_committee = "app"
robotics_committee = "robotics"
web_committee = "web"
ai_committee = "ai"
game_committee = "game"

all_committees = [app_committee, robotics_committee, web_committee, ai_committee, game_committee]

myfirebase = ids.myfirebase


def get_committee_info(committee):
    if committee == app_committee:
        github_team_id = ids.app_team_id
        slack_channel_id = ids.app_slack_channel_id
    elif committee == robotics_committee:
        github_team_id = ids.robotics_team_id
        slack_channel_id = ids.robotics_slack_channel_id
    elif committee == web_committee:
        github_team_id = ids.web_team_id
        slack_channel_id = None
    elif committee == ai_committee:
        github_team_id = ids.ai_team_id
        slack_channel_id = None
    elif committee == game_committee:
        github_team_id = ids.game_team_id
        slack_channel_id = None
    else:
        github_team_id = None
        slack_channel_id = None

    return github_team_id, slack_channel_id


def set_user_committees(my_committees, token):
    user = User.get_member_info(user_id_token=token)

    github_username = user["github_username"]

    for committee in all_committees:
        myfirebase.put('/members/' + user["uid"], "/committees/" + committee, committee in my_committees)

    for committee in my_committees:
        github_team_id, slack_channel_id = get_committee_info(committee)

        if github_team_id is not None:
            Github.add_member_to_team(github_username, github_team_id)

        if slack_channel_id is not None:
            Slack.invite_user_to_channel(slack_channel_id, email=user["rowan_email"])
