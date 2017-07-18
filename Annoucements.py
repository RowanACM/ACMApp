import ids
import time

myfirebase = ids.myfirebase


def make_annoucement(title, message, imageUrl=None, alsoPostOnSlack=False):
    print "COMING SOON"


def get_shortened(text):
    return text[0:200]


def get_announcements():
    raw = myfirebase.get("/", "announcements")

    announcements = []
    for key, value in raw.iteritems():

        icon = "https://firebasestorage.googleapis.com/v0/b/rowan-acm.appspot.com/o/rowanacm.png?alt=media&token=1bf2df32-0d08-4789-a059-7acd00ed19c3"
        if value["committee"] == "Web Dev":
            icon = "https://firebasestorage.googleapis.com/v0/b/rowan-acm.appspot.com/o/web-committee.png?alt=media&token=867937e1-5c83-44b7-aac2-15d7fbb418da"
        elif value["committee"] == "App Committee":
            icon = "https://firebasestorage.googleapis.com/v0/b/rowan-acm.appspot.com/o/app-committee.png?alt=media&token=60ede354-3dfc-4a4a-b10b-34ce92779165"
        elif value["committee"] == "Robotics Committee":
            icon = "https://firebasestorage.googleapis.com/v0/b/rowan-acm.appspot.com/o/robotics-committee.png?alt=media&token=c3fc8f64-696a-4f83-8c01-f3bdd11bd369"
        elif value["committee"] == "AI Committee":
            icon = "https://firebasestorage.googleapis.com/v0/b/rowan-acm.appspot.com/o/ai-committee.png?alt=media&token=543ac18f-134c-4fe2-a057-9501abe48d85"
        elif value["committee"] == "Animation/Game Design":
            icon = "https://firebasestorage.googleapis.com/v0/b/rowan-acm.appspot.com/o/game-committee.png?alt=media&token=21c8d9e6-7c15-48be-a045-82c538fcc0c4"

        announce = {"committee": value["committee"],
                    "icon": icon,
                    "title": value["title"],
                    "text": value["text"],
                    "snippet": get_shortened(value["text"]),
                    "timestamp": value["timestamp"],
                    "url": None}

        announcements.append(announce)

    return announcements
