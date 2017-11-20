import ids
import time
import Slack

myfirebase = ids.myfirebase


def make_announcement(title, body, author=None, snippet=None, committee=None, icon_url=None, url=None, also_post_on_slack=False):
    """
    Post an announcement on the website and app
    :param title: Title of the announcement
    :param body: Full body of the message
    :param author: Name of the person who made the announcement
    :param snippet: What to show before clicking for the full text. If None, take the beginning of body
    :param committee: general, app, web, robotics, ai, game
    :param icon_url: If 'None', an icon will be chosen for you (recommended) To choose your own, specify it here
    :param url: (Optional) A link to include with the announcement
    :param also_post_on_slack: If true, post on slack to the appropriate channel
    :return: void
    """

    if committee is None:
        committee = "general"

    committee_name = get_committee_name(committee)
    if icon_url is None:
        icon_url = get_announcement_icon(committee)

    if snippet is None:
        snippet = get_shortened(body)

    announce = {"author": author,
                "committee": committee_name,
                "committee_id": committee,
                "icon": icon_url,
                "title": title,
                "text": body,
                "snippet": snippet,
                "timestamp": int(time.time()),
                "url": url}

    myfirebase.post("/announcements", announce)

    if also_post_on_slack:
        Slack.post_announcement_on_slack(announce)


def get_shortened(text):
    return text[0:200]


def get_announcement_icon(committee):
    icon = "https://firebasestorage.googleapis.com/v0/b/rowan-acm.appspot.com/o/rowanacm.png?alt=media&token=1bf2df32-0d08-4789-a059-7acd00ed19c3"
    if committee == "web":
        icon = "https://firebasestorage.googleapis.com/v0/b/rowan-acm.appspot.com/o/web-committee.png?alt=media&token=867937e1-5c83-44b7-aac2-15d7fbb418da"
    elif committee == "app":
        icon = "https://firebasestorage.googleapis.com/v0/b/rowan-acm.appspot.com/o/app-committee.png?alt=media&token=60ede354-3dfc-4a4a-b10b-34ce92779165"
    elif committee == "robotics":
        icon = "https://firebasestorage.googleapis.com/v0/b/rowan-acm.appspot.com/o/robotics-committee.png?alt=media&token=c3fc8f64-696a-4f83-8c01-f3bdd11bd369"
    elif committee == "ai":
        icon = "https://firebasestorage.googleapis.com/v0/b/rowan-acm.appspot.com/o/ai-committee.png?alt=media&token=543ac18f-134c-4fe2-a057-9501abe48d85"
    elif committee == "game":
        icon = "https://firebasestorage.googleapis.com/v0/b/rowan-acm.appspot.com/o/game-committee.png?alt=media&token=21c8d9e6-7c15-48be-a045-82c538fcc0c4"
    return icon


def get_announcements():
    raw = myfirebase.get("/", "announcements")

    announcements = []
    for key, value in raw.iteritems():

        if "icon" in value:
            icon = value["icon"]
        else:
            icon = get_announcement_icon(value["committee_id"])

        announce = {"author": value.get("author"),
                    "committee": value["committee"],
                    "committee_id": value["committee_id"],
                    "icon": icon,
                    "title": value["title"],
                    "text": value["text"],
                    "snippet": get_shortened(value["text"]),
                    "timestamp": value["timestamp"],
                    "url": value.get("url")}

        announcements.append(announce)

    announcements = sorted(announcements, key=lambda k: k['timestamp'])
    announcements.reverse()

    return announcements


def get_committee_name(committee_id):
    committee_id = committee_id.lower()
    if committee_id == "general" or committee_id == "all":
        return "General"
    if committee_id == "web":
        return "Web Dev"
    elif committee_id == "app":
        return "App Committee"
    elif committee_id == "robotics":
        return "Robotics Committee"
    elif committee_id == "ai":
        return "AI Committee"
    elif committee_id == "game":
        return "Animation/Game Design"
    return None
