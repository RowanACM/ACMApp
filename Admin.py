import ids

myfirebase = ids.myfirebase


def is_attendance_enabled():
    return myfirebase.get("/", "attendance/status/enabled")


def enable_attendance():
    myfirebase.put("/", "attendance/status/enabled", True)
    print("ENABLED")


def disable_attendance():
    myfirebase.put("/", "attendance/status/enabled", False)
    print("DISABLED")


def set_current_meeting(key):
    myfirebase.put("/", "attendance/status/current", key)
    print("SET", key)
