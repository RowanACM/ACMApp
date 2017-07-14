from __future__ import print_function
from github import NamedUser
from joblib import Parallel, delayed
import multiprocessing
import ids


acm = ids.github.get_organization("rowanacm")


def add_username_to_members(username):
    members_team = acm.get_team(ids.github_members_team_id)
    user = ids.github.get_user(username)
    return members_team.add_to_members(user)


def add_member_to_team(username, team_id):
    team = acm.get_team(team_id)
    user = ids.github.get_user(username)
    return team.add_to_members(user)


def get_recursively(search_dict, field):
    """Takes a dict with nested lists and dicts,
    and searches all dicts for a key of the field
    provided.
    """
    fields_found = []

    for key, value in search_dict.iteritems():

        if key == field:
            fields_found.append(value)

        elif isinstance(value, dict):
            results = get_recursively(value, field)
            for result in results:
                fields_found.append(result)

        elif isinstance(value, list):
            for item in value:
                if isinstance(item, dict):
                    more_results = get_recursively(item, field)
                    for another_result in more_results:
                        fields_found.append(another_result)

    return fields_found


def get_split_name(name):
    space_index = name.find(" ")
    first = name[:space_index]
    last = name[space_index + 1:]
    return first, last


def get_users_emails(user, fast=True):
    emails = []

    events = user.get_events()
    event_count = 0
    for event in events:
        event_count += 1
        if fast and event_count > 10:
            break
        email = get_recursively(event._rawData, "email")
        if email is not None:
            for address in email:
                if "@users.noreply.github.com" not in address \
                        and "rowanads" not in address \
                        and not address.endswith(".local") \
                        and not address.endswith(".localdomain"):
                    emails.append(address)
    return emails


def check_github_member(github_member, email):
    is_rowan_email_address = email.endswith("students.rowan.edu")
    if is_rowan_email_address:
        at_index = email.find("@")
        first_initial = email[at_index - 2]
        last_name = email[:at_index - 2]


    #emails = get_users_emails(github_member)
    #if email in emails:
    #    return True

    if is_rowan_email_address:
        name = github_member.name
        if name is not None and " " in name:
            first, last = get_split_name(name)
            if last.lower() == last_name.lower() and first.lower().startswith(first_initial.lower()):
                return True

    return False


def get_github_username(email):
    for member in acm.get_members():
        if check_github_member(member, email):
            return member._login.value
    return None
